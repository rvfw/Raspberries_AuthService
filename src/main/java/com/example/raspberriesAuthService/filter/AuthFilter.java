package com.example.raspberriesAuthService.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Value("${public-paths}")
    private String[] publicPaths;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(Arrays.stream(publicPaths).anyMatch(request.getRequestURI()::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        String userId=request.getHeader("X-User-Id");
        String userRoles=request.getHeader("X-User-Role");
        if(userId==null || userRoles==null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(),"Missing user ID or roles");
            return;
        }
        try{Long.parseLong(userId);}
        catch (NumberFormatException e){
            response.sendError(HttpStatus.UNAUTHORIZED.value(),"Invalid user ID or roles");
            return;
        }
        List<GrantedAuthority> grantedAuthorities = Arrays.stream(userRoles.split(","))
                .map(role->new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
        Authentication auth=new UsernamePasswordAuthenticationToken(
                userId,
                "",
                grantedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request,response);
    }
}
