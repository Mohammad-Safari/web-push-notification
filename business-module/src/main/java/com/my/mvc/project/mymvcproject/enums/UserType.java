package com.my.mvc.project.mymvcproject.enums;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN("admin"),
    USER("user"),
    GUEST("guest");

    private String value;

    UserType(String value) {
        this.value = value;
    }
}
