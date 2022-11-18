package com.my.mvc.project.mymvcproject.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.my.mvc.project.mymvcproject.util.CookieUtil;

import lombok.AllArgsConstructor;

@Controller()
@AllArgsConstructor
public class IndexController {
    private CookieUtil cookieUtil;

    @RequestMapping(path = { "/", "/index**" }, method = RequestMethod.GET)
    public ModelAndView index(HttpServletResponse response, HttpServletRequest request) {
        var mav = new ModelAndView("index");
        if (!cookieUtil.CookieCheck(request.getCookies())) {
            cookieUtil.configureCookie(response::addCookie);
        }
        return mav;

    }

    // second mapping is for resource/file exclusion
    @RequestMapping(path = { "/app", "/app/{path:[^.]*}" }, method = RequestMethod.GET)
    public ModelAndView indexApp(HttpServletResponse response, HttpServletRequest request) {
        var mav = new ModelAndView("forward:/angular-app/index.html");
        if (!cookieUtil.CookieCheck(request.getCookies())) {
            cookieUtil.configureCookie(response::addCookie);
        }
        return mav;
    }
}