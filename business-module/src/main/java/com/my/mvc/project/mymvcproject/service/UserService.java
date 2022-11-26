package com.my.mvc.project.mymvcproject.service;

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
        // broken
        var detailQuery = userDetailsRepository.findById(id);
        if (detailQuery.isEmpty() || userQuery.isEmpty()) {
            throw new UserNotRegisteredDetailsException();
        }
        var foundUser = userQuery.get();
        var foundDetail = detailQuery.get();
        if (foundUser.getUserType() != UserType.USER) {
            throw new UserNotRegisteredDetailsException();
        }
        return foundDetail;
    }
    
    public User getByUsername(String username) {
        var userDetail = UserDetails.builder().username(username).build();
        var example = Example.of(userDetail);
        return userDetailsRepository.findOne(example).orElse(null).getUser();
    }

    public User getByUsername2(String username) {
        var det = userDetailsRepository.findByUsername(username).get(0);
        return det.getUser();
    }

    public void signup(SignupDto signupDto) {
        User user = User.builder().userType(UserType.USER).build();
        userRepository.save(user);
        userDetailsRepository.save(UserDetails.builder()
                .user(user)
                .username(signupDto.getUsername())
                .password(signupDto.getPassword())
                .build());
    }

    public boolean login(LoginDto loginDto) {
        var det = UserDetails.builder()
                .username(loginDto.getUsername())
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
