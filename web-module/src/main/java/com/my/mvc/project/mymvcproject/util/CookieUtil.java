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