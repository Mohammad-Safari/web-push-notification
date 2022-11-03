package com.my.mvc.project.mymvcproject.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.data.repository.UserRepository;
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

    // @Cacheable(value = "id")
    public UserDetails getUserDetails(long id) throws UserNotValidException, UserNotRegisteredDetailsException {
        var user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotValidException();
        }
        var foundUser = user.get();
        if (foundUser.getUserType() != UserType.USER) {
            throw new UserNotRegisteredDetailsException();
        }
        return foundUser.getDetails();
    }

    public long getGuestId() {
        var user = new User();
        userRepository.save(user);
        return user.getId();
    }

    public boolean validateId(long id) {
        return userRepository.existsById(id);
    }

    public User readUserByUsername(String username) {
        return null;
    }

    public void signup(SignupDto signupDto) {
    }
}
