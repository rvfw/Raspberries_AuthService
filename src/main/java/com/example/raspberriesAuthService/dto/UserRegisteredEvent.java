package com.example.raspberriesAuthService.dto;

import com.example.raspberriesAuthService.enums.Role;
import lombok.Getter;

@Getter
public class UserRegisteredEvent {
    private final Long id;
    private final String name;
    private final Role role;
    public UserRegisteredEvent(Long id,String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
