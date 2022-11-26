package com.my.mvc.project.mymvcproject.enums;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN("admin"),
    USER("user"),
    GUEST("guest");

    private String type;

    UserType(String type) {
        this.type = type;
    }
}
