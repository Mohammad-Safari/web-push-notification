package com.my.mvc.project.mymvcproject.filter;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.context.ActionContext;
import com.my.mvc.project.mymvcproject.context.AppContext;
import com.my.mvc.project.mymvcproject.context.UserContext;

import lombok.AllArgsConstructor;

@Component
@Order(0)
@AllArgsConstructor
public class ContextFilter extends OncePerRequestFilter {
    private AppContext appContext;
    private UserContext userContext;
    private ActionContext actionContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authorization = request.getHeader(ContextConstants.AUTHORIZATION_HEADER_NAME);
        if (authorization == null || !authorization.startsWith(ContextConstants.AUTHORIZATION_HEADER_TYPE)) {
            response.sendError(403, "Authorization header is missing");
            // request.getRequestDispatcher("/error").forward(request, response);
        } else {
            var startIndex = ContextConstants.AUTHORIZATION_HEADER_TYPE.length() + 1;
            var requestContext = new ObjectMapper().readValue(
                    Base64.getDecoder().decode(authorization.substring(startIndex)),
                    UserContext.class);
            userContext.setUsername(requestContext.getUsername());
            userContext.setEmail(requestContext.getEmail());
            actionContext.setName(request.getHeader(ContextConstants.ACTION_HEADER_NAME));
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return !requestURI.startsWith("/api") || requestURI.startsWith("/api/login");
    }
}
