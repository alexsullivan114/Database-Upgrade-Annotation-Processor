package com.alexsullivan;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Created by Alexs on 4/3/2017.
 */

class DBUpgradeCacheWriter {

    static void build(List<DbUpgradeContainer> upgradeContainers, Messager messager, ProcessingEnvironment processingEnvironment, String packageName) {
        Map<Integer, DbUpgradeContainer> containerMap = new HashMap<>();
        for (DbUpgradeContainer container : upgradeContainers) {
            containerMap.put(container.getVersion(), container);
        }

        Map<Integer, DbUpgradeContainer> reconciledMap = reconcileDbUpgradeMap(containerMap, processingEnvironment);

        TypeSpec.Builder upgradeCacheBuilder = TypeSpec.classBuilder("SQLiteUpgradeHelper$$DbUpgradeMap")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(getUpgrades(reconciledMap))
                .addField(getUpgradeMapField());

        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "Size of reconciled map: " + reconciledMap);

        for (DbUpgradeContainer container : reconciledMap.values()) {
            upgradeCacheBuilder.addOriginatingElement(container.getTypeElement());
        }

        JavaFile javaFile = JavaFile.builder(packageName, upgradeCacheBuilder.build())
                .build();
        try {
            buildMappedObjectsResource(reconciledMap, processingEnvironment);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Hit an error while trying to build the static resource map reference file " + e.toString());
        }

        try {
            javaFile.writeTo(processingEnvironment.getFiler());
        } catch (FilerException e) {
            // no-op. Just recreating the file, which is fine for now. In the future if we want to handle other annotation processors using this annotation we'll
            // need to change some stuff.
            messager.printMessage(Diagnostic.Kind.ERROR, "Recreating the file...");
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
        }

    }

    private static Map<Integer, DbUpgradeContainer> reconcileDbUpgradeMap(Map<Integer, DbUpgradeContainer> newProcessorMap, ProcessingEnvironment processingEnvironment) {
        try {
            Map<Integer, DbUpgradeContainer> existingResourceMap = getMappedObjectsResource(processingEnvironment);
            for (Integer i : existingResourceMap.keySet()) {
                // First populate the typeelement on all of the dbupgradecontainers, since GSON can't reconcile these.
                DbUpgradeContainer container = existingResourceMap.get(i);
                container.setTypeElement(processingEnvironment.getElementUtils().getTypeElement(container.getClassName()));
                // Next, make sure all of the previously discovered types still have the annotation.
                if (container.getTypeElement() == null) {
                    processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "Couldn't find type " + container.getClassName() + " Assuming it's been deleted and removing it from the cache.");
                    existingResourceMap.remove(i); // TODO: Concurrent modification? I don't think so since we're looping through the keyset....
                }
            }
            // Finally, add all of our new items if the new items aren't already included in our list.
            for (Integer i : newProcessorMap.keySet()) {
                if (!existingResourceMap.containsKey(i)) {
                    existingResourceMap.put(i, newProcessorMap.get(i));
                }
            }

            return existingResourceMap;

        } catch (IOException e) {
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "Couldn't find the mapped objects file - assuming first run of processor.");
            return newProcessorMap;
        }
    }

    private static Map<Integer, DbUpgradeContainer> getMappedObjectsResource(ProcessingEnvironment processingEnvironment) throws IOException {
        TypeToken type = new TypeToken<Map<Integer, DbUpgradeContainer>>(){};
        FileObject fileObject = processingEnvironment.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "", "MappedObjects");
        return new Gson().fromJson(new JsonReader(fileObject.openReader(true)), type.getType());
    }

    private static void buildMappedObjectsResource(Map<Integer, DbUpgradeContainer> newProcessorMap, ProcessingEnvironment processingEnvironment) throws IOException {
        BufferedWriter writer = new BufferedWriter(processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "MappedObjects").openWriter());
        String serialized = new Gson().toJson(newProcessorMap);
        writer.write(serialized);
        writer.flush();
        writer.close();
    }

    private static MethodSpec getUpgrades(Map<Integer, DbUpgradeContainer> containerMap) {
        MethodSpec.Builder getUpgrades = MethodSpec.methodBuilder("getUpgrades")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(getDbUpgradeType())
                .beginControlFlow("if (upgradeMap.isEmpty())");

        for (Integer i : containerMap.keySet()) {
            getUpgrades.addStatement("upgradeMap.put($L, new $T())", i, containerMap.get(i).getTypeElement());
        }

        getUpgrades.endControlFlow()
                .addStatement("return upgradeMap");

        return getUpgrades.build();
    }

    private static FieldSpec getUpgradeMapField() {
        return FieldSpec.builder(getDbUpgradeType(), "upgradeMap")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("new $T()", getDbUpgradeType())
                .build();
    }

    private static Type getDbUpgradeType() {
        return new TypeToken<HashMap<Integer, SQLiteUpgrade>>() {
        }.getType();
    }
}
