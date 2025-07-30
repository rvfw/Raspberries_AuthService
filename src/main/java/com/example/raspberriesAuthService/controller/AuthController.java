package com.example.raspberriesAuthService.controller;

import com.example.raspberriesAuthService.dto.LoginDto;
import com.example.raspberriesAuthService.dto.RegisterDto;
import com.example.raspberriesAuthService.dto.AuthResponse;
import com.example.raspberriesAuthService.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("auth/register/user")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(registerDto));
    }
    @PostMapping("auth/register/seller")
    public ResponseEntity<AuthResponse> registerSeller(@Valid @RequestBody RegisterDto registerDto) {
        if(registerDto.getTaxId()==null || registerDto.getTaxId().trim().length()<10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tax Id required");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerSeller(registerDto));
    }
    @PostMapping("auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginDto));
    }
    @PostMapping("auth/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.status(HttpStatus.OK).body("Logout");
    }
}
