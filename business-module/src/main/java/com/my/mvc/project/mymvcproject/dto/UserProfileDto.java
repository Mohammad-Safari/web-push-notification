package com.my.mvc.project.mymvcproject.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String username;
    private String email;
    private String firstName;
    private String lastname;
    private String address;
    private String phone;
}
