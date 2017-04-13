package com.example;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class DbUpgradeFactory {
    public static DbUpgradeContainer fromElement(TypeElement element) {
        return new DbUpgradeContainer(element.getQualifiedName().toString(), element.getAnnotation(DBUpgrade.class).version(), element);
    }
}
