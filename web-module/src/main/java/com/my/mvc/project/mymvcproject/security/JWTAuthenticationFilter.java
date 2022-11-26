package com.my.mvc.project.mymvcproject.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.model.UserDetails;
import com.my.mvc.project.mymvcproject.util.CookieUtil;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final SecurityConstants securityConstants;
    private AuthenticationManager authenticationManager;
    private final CookieUtil cookieUtil;

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            var requestStream = request.getInputStream();
            var login = new ObjectMapper().readValue(requestStream, LoginDto.class);
            var authentication = new UsernamePasswordAuthenticationToken(
                    login.getUsername(),
                    login.getPassword(),
                    new ArrayList<>());
            return authenticationManager.authenticate(
                    authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication auth) throws IOException, ServletException {
        var hmac512 = Algorithm.HMAC512(securityConstants.SECRET.getBytes());
        var user = (User) auth.getPrincipal();
        var username = user.getUsername();
        var now = Instant.now();
        var dateExpiration = now.plusMillis(securityConstants.TOKEN_EXPIRATION_TIME);
        var authority = ((GrantedAuthority) auth.getAuthorities().toArray()[0]);
        var token = JWT.create()
                .withSubject(username)
                .withIssuer(securityConstants.TOKEN_ISSUER)
                .withClaim("role", authority.getAuthority())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(dateExpiration))
                .sign(hmac512);
        cookieUtil.set(response::addCookie,
                securityConstants.AUTHORIZATION_HEADER_NAME, token);
        response.addHeader(securityConstants.AUTHORIZATION_HEADER_NAME,
                securityConstants.TOKEN_PREFIX.concat(token));
    }

}