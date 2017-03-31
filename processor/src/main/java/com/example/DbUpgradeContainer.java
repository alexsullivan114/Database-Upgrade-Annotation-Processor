package com.example;

public class DbUpgradeContainer {
    private final String className;
    private final int version;

    public DbUpgradeContainer(String className, int version) {
        this.className = className;
        this.version = version;
    }
}
