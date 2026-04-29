package com.lab.entity.enums;

public enum UserStatus {
    ACTIVE("正常"),
    SUSPENDED("暂停"),
    INACTIVE("停用");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
