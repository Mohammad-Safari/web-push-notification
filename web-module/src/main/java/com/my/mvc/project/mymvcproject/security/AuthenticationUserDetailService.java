package com.my.mvc.project.mymvcproject.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationUserDetailService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.my.mvc.project.mymvcproject.model.User user = userService.readUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        com.my.mvc.project.mymvcproject.model.UserDetails userDetail = user.getDetails();
        return new User(userDetail.getUserName(),
                userDetail.getPassword(),
                Collections.emptyList());
    }
}