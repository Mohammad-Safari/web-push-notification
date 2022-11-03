package com.my.mvc.project.mymvcproject.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.dto.SignupDto;
import com.my.mvc.project.mymvcproject.model.User;
import com.my.mvc.project.mymvcproject.service.UserDetailsService;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.AllArgsConstructor;

@RestController()
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    UserService userService;
    UserDetailsService detailsService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return "";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupDto signupDto) {
        userService.signup(signupDto);
        return "";
    }

    @PostMapping("/logout")
    @ResponseBody
    public String logout() {
        return "";
    }
}
