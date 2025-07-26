package com.example.raspberriesAuthService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
    @NotNull(message = "Incorrect email address")
    @Pattern(regexp="^[-a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Incorrect email address")
    private String email;
    @NotNull(message = "Password length from 8 to 32 characters")
    @Size(min = 8, max = 32,message = "Password length from 8 to 32 characters")
    private String password;
    public LoginDto() {}
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
