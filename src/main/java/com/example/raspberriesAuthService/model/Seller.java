package com.example.raspberriesAuthService.model;

import com.example.raspberriesAuthService.enums.Role;
import jakarta.persistence.Entity;

@Entity
public class Seller extends Account{
    private String taxId;
    public Seller(){}
    public Seller(String email, String password, String taxId) {
        super(email,password, Role.ROLE_SELLER);
        this.taxId = taxId;
    }
}
