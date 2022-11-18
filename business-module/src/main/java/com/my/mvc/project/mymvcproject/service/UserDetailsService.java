package com.my.mvc.project.mymvcproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.data.repository.UserRepository;
import com.my.mvc.project.mymvcproject.model.UserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsService {

    private UserRepository userRepo;

    public UserDetails loadUserByUsername(String email) throws Exception {
        // Optional<User> userRes = userRepo.findByEmail(email);
        // if (userRes.isEmpty())
        //     throw new Exception("Could not findUser with email = " + email);
        // User user = userRes.get();
        // return user.getDetails();
        return new UserDetails();
    }
}