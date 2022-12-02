package com.my.mvc.project.mymvcproject.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CookieUtil {

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
        List.of("/event", "/socket").forEach(path -> {
            var cookie = new Cookie(key, value);
            cookie.setPath(path);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookieSetter.accept(cookie);
        });
    }

}