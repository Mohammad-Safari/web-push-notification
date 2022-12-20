package com.my.mvc.project.mymvcproject.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.my.mvc.project.mymvcproject.controller.api.AuthenticationException;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JWTAuthenticationUtil {
        private SecurityConstants securityConstants;
        private AuthenticationManager authenticationManager;

        public Authentication attemptAuthentication(String username, String password) throws AuthenticationException {
                var authentication = new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
                return authenticationManager.authenticate(authentication);
        }

        public String successfulAuthentication(Authentication authentication){
                var hmac512 = Algorithm.HMAC512(securityConstants.SECRET.getBytes());
                var user = (User) authentication.getPrincipal();
                var username = user.getUsername();
                var now = Instant.now();
                var dateExpiration = now.plusMillis(securityConstants.TOKEN_EXPIRATION_TIME);
                var authority = ((GrantedAuthority) authentication.getAuthorities().toArray()[0]);
                return JWT.create()
                                .withSubject(username)
                                .withIssuer(securityConstants.TOKEN_ISSUER)
                                .withClaim("role", authority.getAuthority())
                                .withIssuedAt(Date.from(now))
                                .withExpiresAt(Date.from(dateExpiration))
                                .sign(hmac512);
        }

}