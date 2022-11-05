package com.my.mvc.project.mymvcproject.controller.api;

import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.context.UserContext;
import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.dto.SignupDto;
import com.my.mvc.project.mymvcproject.filter.ContextConstants;
import com.my.mvc.project.mymvcproject.service.UserDetailsService;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@RestController()
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    UserService userService;
    UserDetailsService detailsService;

    @PostMapping("/login")
    @SneakyThrows
    public String login(@RequestBody LoginDto loginDto, HttpServletResponse response, UserContext userContext) {
        userContext.setUsername(loginDto.getUsername());
        var a = new ObjectMapper().writeValueAsString(userContext);
        response.setHeader(ContextConstants.AUTHORIZATION_HEADER_NAME,
                ContextConstants.AUTHORIZATION_HEADER_TYPE + " " + Base64.getEncoder().encodeToString(a.getBytes()));
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
