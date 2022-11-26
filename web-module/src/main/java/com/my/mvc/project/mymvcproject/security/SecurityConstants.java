package com.my.mvc.project.mymvcproject.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConstants {
    @Value("${jwt.secret}")
    String SECRET;
    public final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public final String AUTHORIZATION_HEADER_TYPE = "CUSTOM-AUTH";
    /**
     * token configuration
     */
    public final String TOKEN_ISSUER = "Spring MVC Project";
    public final String TOKEN_PREFIX = AUTHORIZATION_HEADER_TYPE + " ";
    public final long TOKEN_EXPIRATION_TIME = 864000000; // 10 days
    /**
     * api configuration
     */
    public final String LOGIN_URL = "/api/login";
    public final String SIGNUP_URL = "/api/signup";
    public final String API_PATTERN = "/api/**";
    public final String EVENT_PATTERN = "/event/**";
    public final List<String> EXCLUDED_PATTERNS = List.of(LOGIN_URL, SIGNUP_URL, "/event/rbe", "/event/sse",
            "/h2-console/**");
    public final List<String> INCLUDED_PATTERNS = List.of(API_PATTERN, EVENT_PATTERN);
}