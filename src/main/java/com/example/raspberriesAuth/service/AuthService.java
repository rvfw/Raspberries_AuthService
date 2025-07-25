package com.example.raspberriesAuth.service;

import com.example.raspberriesAuth.dto.LoginDto;
import com.example.raspberriesAuth.dto.RegisterDto;
import com.example.raspberriesAuth.dto.AuthResponse;
import com.example.raspberriesAuth.util.JwtUtils;
import com.example.raspberriesAuth.util.PasswordSecurity;
import com.example.raspberriesAuth.model.User;
import com.example.raspberriesAuth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordSecurity passwordSecurity;
    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, PasswordSecurity passwordSecurity) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordSecurity = passwordSecurity;
    }

    public AuthResponse register(RegisterDto registerDto) {
        if(userRepository.findByEmail(registerDto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        User user=new User(registerDto.getEmail(), passwordSecurity.encodePassword(registerDto.getPassword()));
        User createdUser=userRepository.save(user);
        String token= jwtUtils.generateToken(createdUser);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginDto loginDto) {
        var foundedUser=userRepository.findByEmail(loginDto.getEmail());
        if(foundedUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"There is no registered account with this email.");
        }
        if(!passwordSecurity.checkPassword(loginDto.getPassword(),foundedUser.get().getEncodedPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid password");
        }
        String token=jwtUtils.generateToken(foundedUser.get());
        return new AuthResponse(token);
    }
}
