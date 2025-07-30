package com.example.raspberriesAuthService.dto;

import com.example.raspberriesAuthService.enums.Role;
import lombok.Getter;

@Getter
public class SellerRegisteredEvent {
    private final Long id;
    private final String name;
    private final String taxId;
    public SellerRegisteredEvent(Long id, String name, String taxId) {
        this.id = id;
        this.name = name;
        this.taxId = taxId;
    }
}
