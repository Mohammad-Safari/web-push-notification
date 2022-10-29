package com.my.mvc.project.mymvcproject.enums;

public enum UserType {
    ADMIN("admin"),
    USER("user"),
    GUEST("guest");

    private String type;

    UserType(String type) {
        this.type = type;
    }
}
