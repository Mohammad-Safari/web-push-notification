package com.my.mvc.project.mymvcproject.controller.api;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.context.RequestContext;
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
    RequestContext appContext;
    UserService userService;
    UserDetailsService detailsService;

    @PostMapping("/login")
    @SneakyThrows
    public Object login(@RequestBody LoginDto loginDto,
            HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader(ContextConstants.AUTHORIZATION_HEADER_NAME) != null) {
            appContext.getUserContext().setUsername(loginDto.getUsername());
            var a = new ObjectMapper().writeValueAsString(appContext.getUserContext());
            response.setHeader(ContextConstants.AUTHORIZATION_HEADER_NAME,
                    ContextConstants.AUTHORIZATION_HEADER_TYPE + " "
                            + Base64.getEncoder().encodeToString(a.getBytes()));
        }
        return new Object() {
            String state = "Success";
        };
    }

    @PostMapping("/signup")
    public Object signup(@RequestBody SignupDto signupDto) {
        userService.signup(signupDto);
        return new Object() {
            String state = "Success";
        };
    }

    @PostMapping("/logout")
    @ResponseBody
    public Object logout() {
        return new Object() {
            String state = "Success";
        };
    }
}
