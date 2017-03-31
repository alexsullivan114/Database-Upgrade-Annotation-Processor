package com.example;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class AnnotationChecker {
    static boolean properElementAnnotated(Element annotatedElement,
                                          Elements elementUtils,
                                          Types typeUtils) {
        TypeMirror upgrade = elementUtils.getTypeElement("com.example.SQLiteUpgrade").asType();
        boolean correctInterface = typeUtils.isAssignable(annotatedElement.asType(), upgrade);
        // Just make this public for now.
        boolean correctModifiers = annotatedElement.getModifiers().contains(Modifier.PUBLIC);
        boolean hasEmptyConstructor = false;
        for (Element enclosed : annotatedElement.getEnclosedElements()) {
            if (enclosed.getKind().equals(ElementKind.CONSTRUCTOR)) {
                ExecutableElement executableElement = (ExecutableElement)enclosed;
                List<? extends VariableElement> params = executableElement.getParameters();
                if (params.size() == 0) {
                    hasEmptyConstructor = true;
                    break;
                }

            }
        }

        return correctInterface && correctModifiers && hasEmptyConstructor;
    }
}
