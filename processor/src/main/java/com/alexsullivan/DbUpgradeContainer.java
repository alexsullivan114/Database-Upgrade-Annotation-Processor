package com.alexsullivan;

import javax.lang.model.element.TypeElement;

class DbUpgradeContainer {
    private final String className;
    private final int version;
    private transient TypeElement typeElement;

    DbUpgradeContainer(String className, int version, TypeElement typeElement) {
        this.className = className;
        this.version = version;
        this.typeElement = typeElement;
    }

    public String getClassName() {
        return className;
    }

    public int getVersion() {
        return version;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    @Override
    public String toString() {
        return "DbUpgradeContainer{" +
                "className='" + className + '\'' +
                ", version=" + version +
                '}';
    }
}
