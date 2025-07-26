package com.example.raspberriesAuthService.repository;

import com.example.raspberriesAuthService.model.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    @NotNull
    Optional<Account> findById(@NotNull Long id);
    Optional<Account> findByEmail(String email);
}
