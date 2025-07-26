package com.example.raspberriesAuthService.model;

import com.example.raspberriesAuthService.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="account_type")
@Table(name="accounts",indexes={
        @Index(columnList = "email",unique=true),
        @Index(columnList = "role")
})
@Data
public abstract class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String email;
    @Column(nullable=false)
    private String encodedPassword;
    @Column(nullable=false)
    private boolean enabled=true;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;
    protected Account(){}
    protected Account(String email, String password,Role role) {
        this.email = email;
        this.encodedPassword = password;
        this.role=role;
    }
}
