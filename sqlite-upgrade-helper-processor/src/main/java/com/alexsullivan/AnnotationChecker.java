package com.alexsullivan;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class AnnotationChecker {
    private final Elements elementUtils;
    private final Types typeUtils;
    private final Messager messager;

    AnnotationChecker(Elements elementUtils,
                      Types typeUtils,
                      Messager messager) {
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
        this.messager = messager;
    }

    Result isProperDbContainerUpgrade(Element annotatedElement) {
        if (!annotatedElement.getKind().isClass()) {
            return Result.NON_CLASS_DBUPGRADECONTAINER;
        }

        if (!containsPublicModifier(annotatedElement)) {
            return Result.NON_PUBLIC_CLASS;
        }
        else if (!hasEmptyConstructor((TypeElement)annotatedElement)) {
            return Result.NON_EMPTY_CONSTRUCTOR;
        }

        return Result.SUCCESS;
    }

    Result isProperDbUpgrade(Element annotatedElement) {
        if (annotatedElement.getKind() == ElementKind.METHOD) {
            return isProperDbUpgradeMethod((ExecutableElement)annotatedElement);
        }
        else if (annotatedElement.getKind() == ElementKind.CLASS) {
            return isProperDbUpgradeClass((TypeElement)annotatedElement);
        }
        else {
            return Result.NON_CLASS_OR_METHOD;
        }
    }

    private boolean hasEmptyConstructor(TypeElement annotatedElement) {
        for (Element enclosed : annotatedElement.getEnclosedElements()) {
            if (enclosed.getKind().equals(ElementKind.CONSTRUCTOR)) {
                ExecutableElement executableElement = (ExecutableElement)enclosed;
                List<? extends VariableElement> params = executableElement.getParameters();
                if (params.size() == 0) {
                    return true;
                }

            }
        }
        return false;
    }

    private boolean assignable(TypeElement annotatedElement, TypeMirror assignable) {
        return typeUtils.isAssignable(annotatedElement.asType(), assignable);
    }

    private boolean containsPublicModifier(Element annotatedElement) {
        return annotatedElement.getModifiers().contains(Modifier.PUBLIC);
    }

    private Result isProperDbUpgradeClass(TypeElement annotatedElement) {
        TypeMirror dbUpgrade = elementUtils.getTypeElement("com.alexsullivan.SQLiteUpgrade").asType();
        if (!containsPublicModifier(annotatedElement)) {
            return Result.NON_PUBLIC_CLASS;
        }
        else if (!assignable(annotatedElement, dbUpgrade)) {
            return Result.HASNT_IMPLEMENTED_SQLITEUPGRADE;
        }
        else if (!hasEmptyConstructor(annotatedElement)) {
            return Result.NON_EMPTY_CONSTRUCTOR;
        }

        return Result.SUCCESS;
    }

    private Result isProperDbUpgradeMethod(ExecutableElement annotatedElement) {
        if (annotatedElement.getEnclosingElement().getAnnotation(DBUpgradeContainer.class) == null) {
            return Result.METHOD_PARENT_CLASS_NOT_ANNOTATED;
        }
        else if (!typeUtils.isSameType(annotatedElement.getReturnType(), elementUtils.getTypeElement(String.class.getCanonicalName()).asType())) {
            return Result.NON_STRING_RETURN_TYPE;
        }
        else if (!containsPublicModifier(annotatedElement)) {
            return Result.NON_PUBLIC_METHOD;
        }
        else {
            return Result.SUCCESS;
        }
    }

    enum Result {
        SUCCESS(""),
        NON_EMPTY_CONSTRUCTOR("Classes annotated with DBUpgrade or DBUpgradeContainer must have an empty constructor"),
        HASNT_IMPLEMENTED_SQLITEUPGRADE("Classes annoated with DBUpgrade must implement SQLiteUpgrade"),
        NON_PUBLIC_CLASS("Classes annotated with DBUpgrade or DBUpgradeContainer must be public"),
        NON_CLASS_OR_METHOD("Elements marked with DBUpgrade must either be a class or an element"),
        METHOD_PARENT_CLASS_NOT_ANNOTATED("Methods marked with DBUpgrade must live within a DBUpgradeContainer class."),
        NON_STRING_RETURN_TYPE("Methods marked with DBUpgrade must return a String"),
        NON_PUBLIC_METHOD("Methods marked with DBUpgrade must be marked public."),
        NON_CLASS_DBUPGRADECONTAINER("Only classes can be marked with DBUpgradeContainer");

        String message;

        Result(String message) {
            this.message = message;
        }
    }
}
