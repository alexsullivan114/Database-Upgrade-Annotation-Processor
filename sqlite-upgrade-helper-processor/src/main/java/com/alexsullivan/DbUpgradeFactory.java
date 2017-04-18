package com.alexsullivan;

import javax.lang.model.element.TypeElement;

public class DbUpgradeFactory {
    public static DbUpgradeContainer fromElement(TypeElement element) {
        return new DbUpgradeContainer(element.getQualifiedName().toString(), element.getAnnotation(DBUpgrade.class).version(), element);
    }
}
