package com.my.mvc.project.mymvcproject.security;

import java.util.List;
import java.util.stream.Stream;

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
        public final String LOGIN_EP = "/api/login";
        public final String LOGOUT_EP = "/api/logout";
        public final String SIGNUP_EP = "/api/signup";
        public final String CLIENT_PATTERN = "/app/**";
        public final String CLIENT_RESOURCE_PATTERN = "/angular-app/**";
        public final String[] GENERIC_RESOURCE_PATTERN = { "/favicon.ico", "/styles/**", "/font/**", "/img/**" };
        public final String API_PATTERN = "/api/**";
        public final String EVENT_PATTERN = "/event/**";
        public final String SOCKET_PATTERN = "/socket/**";
        /**
         * EXCLUDED_URLS HAVE MORE PRIORITY than INCLUDED_URLS
         */
        public final List<String> INCLUDED_PATTERNS = List.of(API_PATTERN, EVENT_PATTERN, SOCKET_PATTERN);
        public final List<String> EXCLUDED_PATTERNS = Stream
                        .concat(List.of(CLIENT_RESOURCE_PATTERN, CLIENT_PATTERN,
                                        LOGIN_EP, LOGOUT_EP, SIGNUP_EP,
                                        "/event/rbe", "/event/sse", "/h2-console/**").stream(),
                                        List.of(GENERIC_RESOURCE_PATTERN).stream())
                        .toList();
}