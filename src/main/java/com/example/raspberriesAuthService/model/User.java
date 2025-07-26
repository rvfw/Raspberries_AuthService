package com.example.raspberriesAuthService.model;

import com.example.raspberriesAuthService.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
public class User extends Account{
    private String address;
    public User(){}
    public User(String email, String password) {
        super(email,password, Role.ROLE_USER);
    }
}
