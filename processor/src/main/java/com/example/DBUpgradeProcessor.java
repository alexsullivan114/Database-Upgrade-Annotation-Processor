package com.example;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class DBUpgradeProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private List<DbUpgradeContainer> containers = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try
        {
            String packageName = null;
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(DBUpgrade.class)) {
                boolean validClass = AnnotationChecker.properElementAnnotated(annotatedElement, elementUtils, typeUtils);
                if (!validClass)
                {
                    throw new ProcessingException(annotatedElement, "Only classes that implement %s and have a zero-arg constructor can be annotated with @%s",
                            SQLiteUpgrade.class.getSimpleName(), DBUpgrade.class.getSimpleName());
                }

                TypeElement classElement = (TypeElement)annotatedElement;
                DbUpgradeContainer upgradeContainer = DbUpgradeFactory.fromElement(classElement);
                containers.add(upgradeContainer);
                packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
            }

            if (packageName != null) {
                DBUpgradeCacheWriter.build(containers, messager, processingEnv, packageName);
                SQLiteUpgradeHelperWriter.build(processingEnv, packageName);
            }
        }
        catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add("com.example.DBUpgrade");
        return set;
    }

    private void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
