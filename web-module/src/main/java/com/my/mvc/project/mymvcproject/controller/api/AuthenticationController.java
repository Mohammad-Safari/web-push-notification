package com.my.mvc.project.mymvcproject.controller.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.mvc.project.mymvcproject.context.ContextConstants;
import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.dto.SignupDto;
import com.my.mvc.project.mymvcproject.security.JWTAuthenticationUtil;
import com.my.mvc.project.mymvcproject.security.SecurityConstants;
import com.my.mvc.project.mymvcproject.service.UserService;
import com.my.mvc.project.mymvcproject.util.CookieUtil;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthenticationController {
    private JWTAuthenticationUtil authenticationUtil;
    private SecurityConstants securityConstants;
    private RequestContext reqContext;
    private UserService userService;
    private CookieUtil cookieUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDto loginDto,
            HttpServletRequest request, HttpServletResponse response) {
        var authenticationState = State.SUCCESS_STATE_MESSAGE;
        var authorizationToken = "";
        try {
            authorizationToken = authenticate(loginDto, response);
        } catch (AuthenticationException e) {
            authenticationState = State.ERROR_STATE_MESSAGE;
        }
        return Map.of();

    }

    private String authenticate(LoginDto loginDto, HttpServletResponse response) throws AuthenticationException {
        var authorizationToken = "";
        var authentication = authenticationUtil.attemptAuthentication(
                loginDto.getUsername(),
                loginDto.getPassword());
        authorizationToken = authenticationUtil.successfulAuthentication(authentication);

        cookieUtil.set(
                response::addCookie,
                securityConstants.AUTHORIZATION_HEADER_NAME, authorizationToken);
        response.addHeader(
                securityConstants.AUTHORIZATION_HEADER_NAME,
                securityConstants.TOKEN_PREFIX.concat(authorizationToken));
        return authorizationToken;
    }

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody SignupDto signupDto, BCryptPasswordEncoder encoder) {
        var encodedPassword = encoder.encode(signupDto.getPassword());
        signupDto.setPassword(encodedPassword);
        try {
            userService.signup(signupDto);
        } catch (Exception e) {
            return Map.of("state", State.ERROR_STATE_MESSAGE.getState());
        }
        return Map.of("state", State.SUCCESS_STATE_MESSAGE.getState());
    }

    @GetMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {
        cookieUtil.set(response::addCookie, ContextConstants.AUTHORIZATION_HEADER_NAME, "");
        // invalidate token
        return Map.of("state", State.SUCCESS_STATE_MESSAGE.getState());
    }
}

enum State {
    SUCCESS_STATE_MESSAGE("success"),
    ERROR_STATE_MESSAGE("error");

    private String state;

    public String getState() {
        return state;
    }

    State(String state) {
        this.state = state;
    }
}
