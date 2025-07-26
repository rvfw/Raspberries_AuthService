package com.example.raspberriesAuthService.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordSecurity {
    public String encodePassword(String password){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
    public boolean checkPassword (String password, String encodedPassword){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password,encodedPassword);
    }
}
