package com.my.mvc.project.mymvcproject.util;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CookieUtil {
    private UserService service;
    // @Value("${jwt_secret}")
    // private String secret = "jdkjhgkdghdfkgjdkgjhkd";

    // public String generateToken(String email) throws IllegalArgumentException,
    // JWTCreationException {
    // return JWT.create()
    // .withSubject("User Details")
    // .withClaim("email", email)
    // .withIssuedAt(new Date())
    // .withIssuer("Spring MVC Project")
    // .sign(Algorithm.HMAC256(secret));
    // }

    // public String validateTokenAndRetrieveSubject(String token) throws
    // JWTVerificationException {
    // JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
    // .withSubject("User Details")
    // .withIssuer("Spring MVC Project")
    // .build();
    // DecodedJWT jwt = verifier.verify(token);
    // return jwt.getClaim("email").asString();
    // }

    public String get(Cookie[] cookies, String key) {
        if (cookies != null) {
            var cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(key)).findFirst();
            if (cookie.isPresent()) {
                return cookie.get().getValue();
            }
        }
        return "";
    }

    public void set(Consumer<Cookie> cookieSetter, String key, String value) {
        var cookie = new Cookie(key, value);
        cookie.setPath("/event");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        var socketCookie = new Cookie(key, value);
        cookie.setPath("/socket");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookieSetter.accept(socketCookie);
    }

}