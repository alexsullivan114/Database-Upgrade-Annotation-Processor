package com.alexsullivan;

import com.google.common.reflect.TypeToken;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.ClassName.get;

/**
 * Created by Alexs on 4/4/2017.
 */

public class SQLiteUpgradeHelperWriter {
    static void build(ProcessingEnvironment processingEnvironment, String packageName) {

        TypeSpec sqliteupgradehelper = TypeSpec.classBuilder("SQLiteUpgradeHelper")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(get("android.database.sqlite", "SQLiteOpenHelper"))
                .addField(versionField())
                .addMethod(firstConstructor())
                .addMethod(secondConstructor())
                .addMethod(onCreate(packageName))
                .addMethod(onUpgrade(packageName))
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, sqliteupgradehelper)
                .build();

        try {
            javaFile.writeTo(processingEnvironment.getFiler());
        } catch (FilerException e) {
            // no-op. Just recreating the file, which is fine for now. In the future if we want to handle other annotation processors using this annotation we'll
            // need to change some stuff.
        } catch (IOException e) {
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
        }
    }

    private static FieldSpec versionField() {
        return FieldSpec.builder(TypeName.INT, "newVersion", Modifier.PRIVATE, Modifier.FINAL).build();
    }

    private static MethodSpec firstConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(get("android.content", "Context"), "context")
                .addParameter(String.class, "name")
                .addParameter(get("android.database.sqlite.SQLiteDatabase", "CursorFactory"), "factory")
                .addParameter(TypeName.INT, "version")
                .addStatement("super(context, name, factory, version)")
                .addStatement("newVersion = version")
                .build();
    }

    private static MethodSpec secondConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(get("android.content", "Context"), "context")
                .addParameter(String.class, "name")
                .addParameter(get("android.database.sqlite.SQLiteDatabase", "CursorFactory"), "factory")
                .addParameter(TypeName.INT, "version")
                .addParameter(get("android.database", "DatabaseErrorHandler"), "errorHandler")
                .addStatement("super(context, name, factory, version, errorHandler)")
                .addStatement("newVersion = version")
                .build();
    }

    private static MethodSpec onCreate(String packageName) {
        return MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(get("android.database.sqlite", "SQLiteDatabase"), "sqliteDatabase")
                .addStatement("$T upgradeMap = $T.getUpgrades()", dbMapType(), get(packageName, "SQLiteUpgradeHelper$$DbUpgradeMap"))
                .beginControlFlow("for (int i = 1; i <= newVersion; i++)")
                .beginControlFlow("if (upgradeMap.containsKey(i))")
                .addStatement("sqliteDatabase.execSQL(upgradeMap.get(i).upgradeScript())")
                .endControlFlow()
                .endControlFlow()
                .build();
    }

    private static MethodSpec onUpgrade(String packageName) {
        return MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(get("android.database.sqlite", "SQLiteDatabase"), "sqliteDatabase")
                .addParameter(TypeName.INT, "oldVersion")
                .addParameter(TypeName.INT, "newVersion")
                .addStatement("$T upgradeMap = $T.getUpgrades()", dbMapType(), get(packageName, "SQLiteUpgradeHelper$$DbUpgradeMap"))
                .beginControlFlow("for (int k = oldVersion + 1; k <= newVersion; k++)")
                .beginControlFlow("if (upgradeMap.containsKey(k))")
                .addStatement("sqliteDatabase.execSQL(upgradeMap.get(k).upgradeScript())")
                .endControlFlow()
                .endControlFlow()
                .build();
    }

    private static Type dbMapType() {
        return new TypeToken<HashMap<Integer, SQLiteUpgrade>>() {
        }.getType();
    }
}
