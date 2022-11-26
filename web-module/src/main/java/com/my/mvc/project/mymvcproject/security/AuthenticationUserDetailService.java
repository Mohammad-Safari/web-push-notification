package com.my.mvc.project.mymvcproject.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.exceptions.UserNotRegisteredDetailsException;
import com.my.mvc.project.mymvcproject.exceptions.UserNotValidException;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationUserDetailService implements UserDetailsService {
    private final UserService userService;
    private final com.my.mvc.project.mymvcproject.service.UserDetailsService userDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var user = userService.getByUsername2(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            var details = userDetailsService.getUserDetails(username);
            var gauth = new SimpleGrantedAuthority(user.getUserType().getValue());
            return new User(username, details.getPassword(), List.of(gauth));
        } catch (UserNotValidException | UserNotRegisteredDetailsException e) {
            throw new UsernameNotFoundException(username);
        }
    }
}