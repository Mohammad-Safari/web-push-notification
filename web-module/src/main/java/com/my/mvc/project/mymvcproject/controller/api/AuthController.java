package com.my.mvc.project.mymvcproject.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.dto.SignupDto;

import lombok.AllArgsConstructor;

@RestController()
@RequestMapping("/login")
@AllArgsConstructor
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return "";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupDto signupDto) {
        return "";
    }

    @PostMapping("/logout")
    @ResponseBody
    public String logout() {
        return "";
    }
}
