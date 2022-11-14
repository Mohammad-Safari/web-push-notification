package com.my.mvc.project.mymvcproject.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    @OneToOne
    @JoinColumn(name = "id")
    private User user;
    @Id
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastname;
    private String address;
    private String phone;
    private Date dateOfBirth;

}