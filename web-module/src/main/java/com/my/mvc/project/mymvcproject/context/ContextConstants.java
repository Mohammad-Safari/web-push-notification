package com.my.mvc.project.mymvcproject.context;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)

public class ContextConstants {
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String AUTHORIZATION_HEADER_TYPE = "CUSTOM-AUTH";
    public static final String ACTION_HEADER_NAME = "Action";
}