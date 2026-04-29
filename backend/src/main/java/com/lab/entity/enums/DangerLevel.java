package com.lab.entity.enums;

public enum DangerLevel {
    LOW("低危"),
    MEDIUM("中危"),
    HIGH("高危"),
    EXTREME("极危");

    private final String description;

    DangerLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
