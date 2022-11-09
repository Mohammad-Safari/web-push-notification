package com.my.mvc.project.mymvcproject.context;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ActionContext {
    String name;
    String state;

}
