package com.example.raspberriesAuth.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name="users",indexes = @Index(columnList = "email",unique=true))
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String email;
    @Column(nullable=false)
    private String encodedPassword;
    @Column(nullable=false)
    private boolean enabled=true;

    @ElementCollection(fetch=FetchType.EAGER)
    private Set<String> roles=new HashSet<>();
    @CreationTimestamp
    private LocalDateTime creationDate;
    public User(){}
    public User(String email, String encodedPassword) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        roles.add("ROLE_USER");
    }
}
