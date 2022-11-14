package com.my.mvc.project.mymvcproject.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Data;

@Data
@Component
@RequestScope
public class ActionContext {
    String name;
    String state;

}
