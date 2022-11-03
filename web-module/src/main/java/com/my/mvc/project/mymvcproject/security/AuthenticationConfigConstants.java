package com.my.mvc.project.mymvcproject.security;

public class AuthenticationConfigConstants {
    public static final String SECRET = "My_Secret";
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/signup";
}