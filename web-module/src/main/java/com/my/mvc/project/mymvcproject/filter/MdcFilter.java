package com.my.mvc.project.mymvcproject.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MdcFilter extends OncePerRequestFilter {
    final String PROXY_FORWARD_HEADER = "X-FORWARDED-FOR";
    final String REQUEST_ID_HEADER = "X-HEADER-TOKEN";
    final String RESPONSE_ID_HEADER = REQUEST_ID_HEADER;

    final String CLIENT_IP_MDC_IDENTIFIER = "clientIP";
    final String REQUEST_ID_MDC_IDENTIFIER = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (request != null) {
            final String token;
            String clientIp = "";
            // [reverse] proxy identifier of request ip origination
            clientIp = request.getHeader(PROXY_FORWARD_HEADER);
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }
            MDC.put(CLIENT_IP_MDC_IDENTIFIER, clientIp);
            if (!StringUtils.hasText(REQUEST_ID_HEADER) && StringUtils.hasText(request.getHeader(REQUEST_ID_HEADER))) {
                token = request.getHeader(REQUEST_ID_HEADER);
            } else {
                token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            }
            MDC.put(REQUEST_ID_MDC_IDENTIFIER, token);
            if (StringUtils.hasText(RESPONSE_ID_HEADER)) {
                response.addHeader(RESPONSE_ID_HEADER, token);
            }
            filterChain.doFilter(request, response);
            MDC.remove(REQUEST_ID_MDC_IDENTIFIER);
            MDC.remove(CLIENT_IP_MDC_IDENTIFIER);
        }
    }

}
