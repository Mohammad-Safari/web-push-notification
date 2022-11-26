package com.my.mvc.project.mymvcproject.controller.api;

import java.util.Base64;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.context.UserContext;
import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.dto.SignupDto;
import com.my.mvc.project.mymvcproject.filter.ContextConstants;
import com.my.mvc.project.mymvcproject.service.UserDetailsService;
import com.my.mvc.project.mymvcproject.service.UserService;
import com.my.mvc.project.mymvcproject.util.CookieUtil;
import com.my.mvc.project.mymvcproject.util.RawResponseDto;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    private UserDetailsService detailsService;
    private RequestContext reqContext;
    private UserService userService;
    private CookieUtil cookieUtil;
    private ObjectMapper mapper;

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

    @PostMapping("/login")
    public RawResponseDto login(@RequestBody LoginDto loginDto,
            HttpServletRequest request, HttpServletResponse response) {
        Function<String, String> tokenHolder = request::getHeader;
        var authenticationState = State.SUCCESS_STATE_MESSAGE;
        var authorizationToken = "";
        try {
            authorizationToken = authenticate(loginDto, tokenHolder.apply(ContextConstants.AUTHORIZATION_HEADER_NAME));
            response.setHeader(ContextConstants.AUTHORIZATION_HEADER_NAME, authorizationToken);
            cookieUtil.set(response::addCookie, ContextConstants.AUTHORIZATION_HEADER_NAME, authorizationToken
                    .substring(ContextConstants.AUTHORIZATION_HEADER_TYPE.length() + 1));
        } catch (Exception e) {
            authenticationState = State.ERROR_STATE_MESSAGE;
        }
        return new RawResponseDto(authenticationState.getState(), authorizationToken) {
            String state;
            String authorization;

            @Override
            public void init(String... args) {
                state = args[0];
                authorization = args[1];
            }
        };
    }

    // TODO move to authentication service
    private String authenticate(LoginDto loginDto, String presentToken)
            throws JsonProcessingException, AuthenticationException {
        var headerValue = presentToken;
        // TODO we assume header is always trusted :)
        if (!StringUtils.hasText(headerValue)) {
            var userContext = reqContext.getUserContext();
            userContext.setUsername(loginDto.getUsername());
            ObjectWriter writer = mapper.writerFor(UserContext.class); // jackson does not know target class is proxied
            headerValue = ContextConstants.AUTHORIZATION_HEADER_TYPE + " "
                    + Base64.getEncoder().encodeToString(
                            writer.writeValueAsString(userContext).getBytes());
        }
        return headerValue;
    }

    @PostMapping("/signup")
    public RawResponseDto signup(@RequestBody SignupDto signupDto, BCryptPasswordEncoder encoder) {
        String encodedPassword = encoder.encode(signupDto.getPassword());
        signupDto.setPassword(encodedPassword);
        userService.signup(signupDto);
        return new RawResponseDto(State.SUCCESS_STATE_MESSAGE.getState()) {
            String state;

            @Override
            public void init(String... args) {
                state = args[0];
            }
        };
    }

    @GetMapping("/logout")
    public RawResponseDto logout(HttpServletResponse response) {
        cookieUtil.set(response::addCookie, ContextConstants.AUTHORIZATION_HEADER_NAME, "");
        // invalidate token
        return new RawResponseDto(State.ERROR_STATE_MESSAGE.getState()) {
            String state;

            @Override
            public void init(String... args) {
                state = args[0];
            }
        };
    }
}
