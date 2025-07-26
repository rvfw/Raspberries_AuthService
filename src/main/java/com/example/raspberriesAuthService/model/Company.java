package com.example.raspberriesAuthService.model;

import com.example.raspberriesAuthService.enums.Role;
import jakarta.persistence.Entity;

@Entity
public class Company extends Account{
    private String taxId;
    public Company(){}
    public Company(String email, String password, String taxId) {
        super(email,password, Role.ROLE_COMPANY);
        this.taxId = taxId;
    }
}
