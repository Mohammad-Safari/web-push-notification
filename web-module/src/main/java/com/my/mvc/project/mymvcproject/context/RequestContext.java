package com.my.mvc.project.mymvcproject.context;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
@AllArgsConstructor
class RequestContextImp implements RequestContext {
    private UserContext userContext;
    private ActionContext actionContext;
}

public interface RequestContext {
    public UserContext getUserContext();

    public ActionContext getActionContext();
}
