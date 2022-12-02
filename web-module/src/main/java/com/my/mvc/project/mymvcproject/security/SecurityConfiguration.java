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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.my.mvc.project.mymvcproject.enums.UserType;
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
    public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
            AuthenticationUserDetailsService userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    UsernamePasswordAuthenticationFilter authenFilter(AuthenticationManager authenticationManager) {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(securityConstants,
                cookieUtil);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl(securityConstants.LOGIN_URL);
        return jwtAuthenticationFilter;
    }

    @Bean
    BasicAuthenticationFilter authorFilter(AuthenticationManager authenticationManager) {
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(securityConstants,
                authenticationManager, cookieUtil);
        return jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JWTAuthenticationFilter authenFilter,
            JWTAuthorizationFilter authorFilter) throws Exception {
        // h2console
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll()
                .and().headers().frameOptions().disable();
        http.cors().and().csrf().disable()
                .authorizeRequests()
                // .antMatchers(HttpMethod.POST,
                // securityConstants.SIGN_UP_URL).permitAll()
                .antMatchers(securityConstants.API_PATTERN, securityConstants.EVENT_PATTERN)
                .hasAnyAuthority(UserType.USER.getValue())
                .anyRequest().permitAll()
                .and()
                .addFilter(authenFilter)
                .addFilter(authorFilter)
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

}
