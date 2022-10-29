package com.my.mvc.project.mymvcproject.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    @Id
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private Date dateOfBirth;

}