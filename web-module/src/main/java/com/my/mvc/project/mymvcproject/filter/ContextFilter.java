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
import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.context.UserContext;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@Order(0)
public class ContextFilter extends OncePerRequestFilter {
    private RequestContext reqContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authorization = request.getHeader(ContextConstants.AUTHORIZATION_HEADER_NAME);
        if (authorization == null || !authorization.startsWith(ContextConstants.AUTHORIZATION_HEADER_TYPE)) {
            response.sendError(403, "Authorization header is missing");
            // request.getRequestDispatcher("/error").forward(request, response);
        } else {
            var startIndex = ContextConstants.AUTHORIZATION_HEADER_TYPE.length() + 1;
            var jsonToken = new String(Base64.getDecoder().decode(authorization.substring(startIndex)));
            var requestContext = new ObjectMapper().readValue(jsonToken, UserContext.class);
            reqContext.getUserContext().setUsername(requestContext.getUsername());
            reqContext.getUserContext().setEmail(requestContext.getEmail());
            reqContext.getActionContext().setName(request.getHeader(ContextConstants.ACTION_HEADER_NAME));
        }
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
