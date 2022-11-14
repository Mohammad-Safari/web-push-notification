package com.my.mvc.project.mymvcproject.context;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@AllArgsConstructor
public class RequestContext {
    private UserContext userContext;
    private ActionContext actionContext;
}
