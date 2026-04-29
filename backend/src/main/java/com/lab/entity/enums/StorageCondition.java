package com.lab.entity.enums;

public enum StorageCondition {
    NORMAL("常温"),
    REFRIGERATED("冷藏"),
    FROZEN("冷冻"),
    DARK("避光"),
    VENTILATED("通风"),
    DRY("干燥");

    private final String description;

    StorageCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
