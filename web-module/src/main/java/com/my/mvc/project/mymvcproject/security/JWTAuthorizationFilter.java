package com.my.mvc.project.mymvcproject.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.my.mvc.project.mymvcproject.util.CookieUtil;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private SecurityConstants securityConstants;
    private AuthenticationManager authenticationManager;
    private CookieUtil cookieUtil;
    private AntPathMatcher antPathMatcher;

    private Predicate<String> tokenFormatCheck = (content) -> StringUtils.hasText(content);
    private List<Function<HttpServletRequest, String>> suppliers = List.of(
            (request) -> StringUtils.replace(
                    request.getHeader(securityConstants.AUTHORIZATION_HEADER_NAME),
                    securityConstants.TOKEN_PREFIX, ""),
            (request) -> cookieUtil.get(request.getCookies(), securityConstants.AUTHORIZATION_HEADER_NAME));

    public JWTAuthorizationFilter(SecurityConstants authenticationConfigConstants,
            AuthenticationManager authenticationManager, CookieUtil cookieUtil, AntPathMatcher antPathMatcher) {
        super(authenticationManager);
        this.securityConstants = authenticationConfigConstants;
        this.authenticationManager = authenticationManager;
        this.cookieUtil = cookieUtil;
        this.antPathMatcher = antPathMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var authorization = suppliers.stream().map(fn -> fn.apply(request)).filter(tokenFormatCheck).findFirst().orElse("");
        try {
            var authentication = parseToken(authorization);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JWTVerificationException e) {
            // response.sendError(403);
        } finally {
            chain.doFilter(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken parseToken(String token) throws JWTVerificationException {
        var hmac512 = Algorithm.HMAC512(securityConstants.SECRET.getBytes());
        var jwt = JWT.require(hmac512).build().verify(token);
        var user = jwt.getSubject();
        var role = jwt.getClaim("role").as(String.class);
        var issuer = jwt.getIssuer();
        if (StringUtils.hasText(user) && StringUtils.hasText(issuer)
                && issuer.equals(securityConstants.TOKEN_ISSUER)) {
            var parsedToken = new UsernamePasswordAuthenticationToken(user, null,
                    List.of(new SimpleGrantedAuthority(role)));
            return parsedToken;
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        if (!securityConstants.EXCLUDED_PATTERNS.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, requestURI))) {

            if (securityConstants.INCLUDED_PATTERNS.stream()
                    .anyMatch(pattern -> antPathMatcher.match(pattern, requestURI))) {
                return false;
            }
        }
        return true;
    }
}