package com.my.mvc.project.mymvcproject.security;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.my.mvc.project.mymvcproject.util.CookieUtil;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private SecurityConstants securityConstants;
    private AuthenticationManager authenticationManager;
    private CookieUtil cookieUtil;

    public JWTAuthorizationFilter(SecurityConstants authenticationConfigConstants,
            AuthenticationManager authenticationManager, CookieUtil cookieUtil) {
        super(authenticationManager);
        this.securityConstants = authenticationConfigConstants;
        this.authenticationManager = authenticationManager;
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authorization = null;

        Predicate<String> contentCheck = (content) -> StringUtils.hasText(content)
                && content.startsWith(securityConstants.AUTHORIZATION_HEADER_TYPE);
        List<Supplier<String>> suppliers = List.of(
                () -> StringUtils.replace(
                        request.getHeader(securityConstants.AUTHORIZATION_HEADER_NAME),
                        securityConstants.TOKEN_PREFIX, ""),
                () -> cookieUtil.get(request.getCookies(), securityConstants.AUTHORIZATION_HEADER_NAME));

        var it = suppliers.iterator();
        while (it.hasNext() && !contentCheck.test(authorization)) {
            Supplier<String> supplier = it.next();
            authorization = supplier.get();
        }

        UsernamePasswordAuthenticationToken authentication = parseToken(authorization);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken parseToken(String token) {
        var hmac512 = Algorithm.HMAC512(securityConstants.SECRET.getBytes());
        var jwt = JWT.require(hmac512).build().verify(token);
        var user = jwt.getSubject();
        var role = jwt.getClaim("ROLE").as(String.class);
        var issuer = jwt.getIssuer();
        if (StringUtils.hasText(user) && StringUtils.hasText(issuer) && issuer.equals(securityConstants.TOKEN_ISSUER)) {
            var parsedToken = new UsernamePasswordAuthenticationToken(user, null,
                    List.of(new SimpleGrantedAuthority(role)));
            return parsedToken;
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        for (var pattern : securityConstants.EXCLUDED_PATTERNS) {
            if (requestURI.startsWith(pattern))
                return true;
        }
        for (var pattern : securityConstants.INCLUDED_PATTERNS) {
            if (requestURI.startsWith(pattern))
                return true;
        }
        return false;
    }
}
// https://javatodev.com/spring-boot-jwt-authentication/
// https://www.tutorialspoint.com/spring_security/spring_security_with_jwt.htm
// https://www.baeldung.com/spring-security-oauth