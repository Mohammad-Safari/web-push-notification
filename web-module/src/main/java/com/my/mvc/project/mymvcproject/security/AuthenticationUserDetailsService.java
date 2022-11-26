package com.my.mvc.project.mymvcproject.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final com.my.mvc.project.mymvcproject.service.UserDetailsService detService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.my.mvc.project.mymvcproject.model.User user = userService.getByUsername2(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        com.my.mvc.project.mymvcproject.model.UserDetails userDetails;
        try {
            userDetails = detService.loadUserByUsername(username);
            return new User(userDetails.getUsername(),
                    userDetails.getPassword(),
                    Collections.emptyList());
        } catch (Exception e) {
            throw new UsernameNotFoundException(username);
        }
    }
}