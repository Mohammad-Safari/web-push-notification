package com.my.mvc.project.mymvcproject.service;

import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.data.repository.UserDetailsRepository;
import com.my.mvc.project.mymvcproject.data.repository.UserRepository;
import com.my.mvc.project.mymvcproject.enums.UserType;
import com.my.mvc.project.mymvcproject.exceptions.UserNotRegisteredDetailsException;
import com.my.mvc.project.mymvcproject.exceptions.UserNotValidException;
import com.my.mvc.project.mymvcproject.model.UserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsService {

    private UserRepository userRepository;
    private UserDetailsRepository userDetailsRepository;

    public UserDetails getUserDetails(String username) throws UserNotValidException, UserNotRegisteredDetailsException {
        var detailQuery = userDetailsRepository.findByUsername(username);
        if (detailQuery.isEmpty()) {
            throw new UserNotRegisteredDetailsException();
        }
        var foundDetail = detailQuery.get(0);
        var foundUser = foundDetail.getUser();
        if (foundUser.getUserType() != UserType.USER) {
            throw new UserNotRegisteredDetailsException();
        }
        return foundDetail;
    }
}