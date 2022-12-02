package com.my.mvc.project.mymvcproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

import com.my.mvc.project.mymvcproject.util.CookieUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {
        private final CookieUtil cookieUtil;
        private final SecurityConstants securityConstants;

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AntPathMatcher antPathMatcher() {
                return new AntPathMatcher();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http,
                        BCryptPasswordEncoder bCryptPasswordEncoder,
                        AuthenticationUserDetailsService userDetailsService)
                        throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class)
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(bCryptPasswordEncoder)
                                .and()
                                .build();
        }

        @Bean
        BasicAuthenticationFilter authorizationFilter(AuthenticationManager authenticationManager,
                        AntPathMatcher antPathMatcher) {
                JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(securityConstants,
                                authenticationManager, cookieUtil, antPathMatcher);
                return jwtAuthorizationFilter;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, JWTAuthenticationUtil authenticationFilter,
                        JWTAuthorizationFilter authorizationFilter) throws Exception {
                // h2console
                http.authorizeRequests().antMatchers("/h2-console/**").permitAll()
                                .and().headers().frameOptions().disable();
                // application
                http.cors().and().csrf().disable()
                                .authorizeRequests()
                                .antMatchers(securityConstants.EXCLUDED_PATTERNS.toArray(String[]::new))
                                .permitAll()
                                .antMatchers(securityConstants.INCLUDED_PATTERNS.toArray(String[]::new))
                                .authenticated()
                                .anyRequest()
                                .permitAll()
                                .and()
                                .addFilter(authorizationFilter)
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                return http.build();
        }

}
