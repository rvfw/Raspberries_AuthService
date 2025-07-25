package com.example.raspberriesAuth.controller;

import com.example.raspberriesAuth.dto.LoginDto;
import com.example.raspberriesAuth.dto.RegisterDto;
import com.example.raspberriesAuth.dto.AuthResponse;
import com.example.raspberriesAuth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerDto));
    }
    @PostMapping("auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginDto));
    }
    @PostMapping("auth/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> logout() {
        return ResponseEntity.status(HttpStatus.OK).body(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
