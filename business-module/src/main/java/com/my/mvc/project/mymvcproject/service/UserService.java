package com.my.mvc.project.mymvcproject.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.data.repository.UserDetailsRepository;
import com.my.mvc.project.mymvcproject.data.repository.UserRepository;
import com.my.mvc.project.mymvcproject.dto.LoginDto;
import com.my.mvc.project.mymvcproject.dto.SignupDto;
import com.my.mvc.project.mymvcproject.enums.UserType;
import com.my.mvc.project.mymvcproject.exceptions.UserNotRegisteredDetailsException;
import com.my.mvc.project.mymvcproject.exceptions.UserNotValidException;
import com.my.mvc.project.mymvcproject.model.User;
import com.my.mvc.project.mymvcproject.model.UserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;
    UserDetailsRepository userDetailsRepository;

    // @Cacheable(value = "id")
    public UserDetails getUserDetails(long id) throws UserNotValidException, UserNotRegisteredDetailsException {
        var userQuery = userRepository.findById(id);
        if (!userQuery.isPresent()) {
            throw new UserNotValidException();
        }
        var foundUser = userQuery.get();
        if (foundUser.getUserType() != UserType.USER) {
            throw new UserNotRegisteredDetailsException();
        }
        return foundUser.getDetails();
    }

    public User loadUserByUsername(String username) {
        var user = new User();
        user.setDetails(UserDetails.builder().userName(username).build());
        var example = Example.of(user);
        return userRepository.findOne(example).orElse(null);
    }

    public User loadUserByUsername2(String username) {
        return userRepository.findByUserName(username).get(0);
    }

    public User loadUserByUsername3(String username) {
        return userRepository.findByDetails(UserDetails.builder().userName(username).build()).get(0);
    }

    public void signup(SignupDto signupDto) {
        userDetailsRepository.save(UserDetails.builder()
                .userName(signupDto.getUsername())
                .password(signupDto.getPassword())
                .build());
    }
    
    public boolean login(LoginDto loginDto) {
        var det = UserDetails.builder()
        .userName(loginDto.getUsername())
        .password(loginDto.getPassword())
        .build();
        return userDetailsRepository.exists(Example.of(det));
    }

    public long getGuestId() {
        var user = new User();
        userRepository.save(user);
        return user.getId();
    }

    public boolean validateId(long id) {
        return userRepository.existsById(id);
    }
}
