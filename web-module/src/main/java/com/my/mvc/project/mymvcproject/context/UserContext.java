package com.my.mvc.project.mymvcproject.context;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class UserContext {
    String username;
    String email;
}
