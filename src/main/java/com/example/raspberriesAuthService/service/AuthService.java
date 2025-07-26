package com.example.raspberriesAuthService.service;

import com.example.raspberriesAuthService.dto.LoginDto;
import com.example.raspberriesAuthService.dto.RegisterDto;
import com.example.raspberriesAuthService.dto.AuthResponse;
import com.example.raspberriesAuthService.enums.Role;
import com.example.raspberriesAuthService.model.Company;
import com.example.raspberriesAuthService.model.User;
import com.example.raspberriesAuthService.util.JwtUtils;
import com.example.raspberriesAuthService.util.PasswordSecurity;
import com.example.raspberriesAuthService.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final JwtUtils jwtUtils;
    private final PasswordSecurity passwordSecurity;
    public AuthService(AccountRepository accountRepository, JwtUtils jwtUtils, PasswordSecurity passwordSecurity) {
        this.accountRepository = accountRepository;
        this.jwtUtils = jwtUtils;
        this.passwordSecurity = passwordSecurity;
    }

    public AuthResponse registerUser(RegisterDto registerDto) {
        if(accountRepository.findByEmail(registerDto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        User user=new User(registerDto.getEmail(), passwordSecurity.encodePassword(registerDto.getPassword()));
        User createdUser= accountRepository.save(user);
        String token= jwtUtils.generateToken(createdUser);
        return new AuthResponse(token);
    }
    public AuthResponse registerCompany(RegisterDto registerDto) {
        if(accountRepository.findByEmail(registerDto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        Company company=new Company(registerDto.getEmail(), passwordSecurity.encodePassword(registerDto.getPassword()),registerDto.getTaxId());
        Company createdCompany= accountRepository.save(company);
        String token= jwtUtils.generateToken(createdCompany);
        return new AuthResponse(token);
    }
    public AuthResponse login(LoginDto loginDto) {
        var foundedAccount= accountRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"There is no registered account with this email."));
        if(!passwordSecurity.checkPassword(loginDto.getPassword(),foundedAccount.getEncodedPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid password");
        }
        String token=jwtUtils.generateToken(foundedAccount);
        return new AuthResponse(token);
    }
}
