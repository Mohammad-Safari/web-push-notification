package com.my.mvc.project.mymvcproject.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.util.CookieUtil;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@Order(0)
public class ContextFilter extends OncePerRequestFilter {
    private RequestContext reqContext;
    private CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var secContext = SecurityContextHolder.getContext().getAuthentication();
        if (secContext == null) {
            return;
        }
        reqContext.getUserContext().setUsername(((String) secContext.getName()));
        reqContext.getUserContext().setEmail("");
        reqContext.getActionContext().setName(request.getHeader(ContextConstants.ACTION_HEADER_NAME));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return !requestURI.startsWith("/api") && !requestURI.startsWith("/event")
                || (requestURI.startsWith("/api/login") || requestURI.startsWith("/api/signup")
                        || (requestURI.startsWith("/event/sse") || requestURI.startsWith("/event/rbe")));
    }
}
