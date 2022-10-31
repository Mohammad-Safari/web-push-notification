package com.my.mvc.project.mymvcproject.util;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CookieUtil {
    private UserService service;
    @Value("${jwt_secret}")
    private String secret = "jdkjhgkdghdfkgjdkgjhkd";

    public String generateToken(String email) throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject("User Details")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withIssuer("Spring MVC Project")
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User Details")
                .withIssuer("Spring MVC Project")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("email").asString();
    }

    public boolean CookieCheck(Cookie[] cookies) {
        if (cookies != null) {
            var cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("id")).findFirst();
            if (cookie.isPresent()
                    && service.validateId(Long.parseLong(cookie.get().getValue()))) {
                return true;
            }
        }
        return false;
    }

    public void configureCookie(Consumer<Cookie> cookieSetter) {
        Cookie cookie = new Cookie("id", String.valueOf(service.getGuestId()));
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookieSetter.accept(cookie);
    }

}