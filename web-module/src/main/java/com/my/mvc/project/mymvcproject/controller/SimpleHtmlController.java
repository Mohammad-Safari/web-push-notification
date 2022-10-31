package com.my.mvc.project.mymvcproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleHtmlController {
    @GetMapping(path = "/page")
    public String getPage() {
        return "page";
    }

}
