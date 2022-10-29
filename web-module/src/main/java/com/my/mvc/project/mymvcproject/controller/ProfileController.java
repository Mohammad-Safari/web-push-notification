package com.my.mvc.project.mymvcproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.my.mvc.project.mymvcproject.dto.UserProfileDto;
import com.my.mvc.project.mymvcproject.exceptions.UserNotRegisteredDetailsException;
import com.my.mvc.project.mymvcproject.exceptions.UserNotValidException;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ProfileController {
    private UserService service;

    @RequestMapping(path = "/profile", method = RequestMethod.GET)
    public String index(Model model, @CookieValue(name = "id", required = false) Long id) {
        try {
            var userProfileDto = new UserProfileDto(service.getUserDetails(id));
            model.addAttribute("profile", userProfileDto);
            return "profile";
        } catch (UserNotValidException e) {
            model.addAttribute("errorMessage", new String("User not valid!"));
            return "error";
        } catch (UserNotRegisteredDetailsException e) {
            model.addAttribute("errorMessage", new String("User not registered details yet!"));
            return "error";
        }

    }
}
