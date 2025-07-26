package com.example.raspberriesAuthService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class RegisterDto {
    @NotNull(message = "Name length from 4 to 16 characters")
    @Size(min = 4, max = 16,message = "Name length from 4 to 16 characters")
    private String name;
    @NotNull(message = "Incorrect email address")
    @Pattern(regexp="^[-a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Incorrect email address")
    private String email;
    @NotNull(message = "Password length from 8 to 32 characters")
    @Size(min = 8, max = 32,message = "Password length from 8 to 32 characters")
    private String password;
    private String taxId;
    public RegisterDto() {}
    public RegisterDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public RegisterDto(String name, String email, String password,String taxId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.taxId=taxId;
    }
}
