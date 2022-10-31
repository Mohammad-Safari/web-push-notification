package com.my.mvc.project.mymvcproject.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.my.mvc.project.mymvcproject.model.UserDetails;
import com.my.mvc.project.mymvcproject.service.UserDetailsService;
import com.my.mvc.project.mymvcproject.util.CookieUtil;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    private CookieUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //         String authHeader = request.getHeader("Authorization");
        // if(authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
        //     String jwt = authHeader.substring(7);
        //     if(jwt == null || jwt.isBlank()){
        //         response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
        //     }else {
        //         try{
        //             String email = jwtUtil.validateTokenAndRetrieveSubject(jwt);
        //             UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        //             // UsernamePasswordAuthenticationToken authToken =
        //             //         new UsernamePasswordAuthenticationToken(email, userDetails.getPassword(), userDetails.getAuthorities());
        //             // if(SecurityContextHolder.getContext().getAuthentication() == null){
        //             //     SecurityContextHolder.getContext().setAuthentication(authToken);
        //             // }
        //         }catch(JWTVerificationException exc){
        //             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
        //         } catch (Exception e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }

        // filterChain.doFilter(request, response);
    }
}