package com.example.raspberriesAuthService.service;

import com.example.raspberriesAuthService.dto.*;
import com.example.raspberriesAuthService.enums.Role;
import com.example.raspberriesAuthService.model.Seller;
import com.example.raspberriesAuthService.model.OutboxEvent;
import com.example.raspberriesAuthService.model.User;
import com.example.raspberriesAuthService.repository.OutboxEventRepository;
import com.example.raspberriesAuthService.util.JwtUtils;
import com.example.raspberriesAuthService.util.PasswordSecurity;
import com.example.raspberriesAuthService.repository.AccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Value("${kafka.topics.user-registered}")
    private String userRegisteredEvent;
    @Value("${kafka.topics.seller-registered}")
    private String sellerRegisteredEvent;
    private final AccountRepository accountRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final JwtUtils jwtUtils;
    private final PasswordSecurity passwordSecurity;
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final ObjectMapper objectMapper=new ObjectMapper();
    public AuthService(AccountRepository accountRepository, JwtUtils jwtUtils, PasswordSecurity passwordSecurity, KafkaTemplate<Long, Object> kafkaTemplate, OutboxEventRepository outboxEventRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountRepository = accountRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.jwtUtils = jwtUtils;
        this.passwordSecurity = passwordSecurity;
    }

    @Transactional
    public AuthResponse registerUser(RegisterDto registerDto) {
        accountRepository.findByEmail(registerDto.getEmail()).ifPresent(e->{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");});
        User user=new User(registerDto.getEmail(), passwordSecurity.encodePassword(registerDto.getPassword()));
        User createdUser= accountRepository.save(user);
        OutboxEvent event;
        try {
            event = new OutboxEvent("USER", createdUser.getId(), userRegisteredEvent, objectMapper.writeValueAsString(
                    new UserRegisteredEvent(createdUser.getId(), registerDto.getName(), Role.ROLE_USER)
            ));
        }catch (JsonProcessingException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed");
        }
        outboxEventRepository.save(event);
        return new AuthResponse(jwtUtils.generateToken(createdUser));
    }
    @Transactional
    public AuthResponse registerSeller(RegisterDto registerDto) {
        if(accountRepository.findByEmail(registerDto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        Seller seller=new Seller(registerDto.getEmail(), passwordSecurity.encodePassword(registerDto.getPassword()),registerDto.getTaxId());
        Seller createdSeller = accountRepository.save(seller);
        OutboxEvent event;
        try {
            event = new OutboxEvent("SELLER", createdSeller.getId(), sellerRegisteredEvent, objectMapper.writeValueAsString(
                    new SellerRegisteredEvent(createdSeller.getId(), registerDto.getName(), registerDto.getTaxId())
            ));
        }catch (JsonProcessingException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller registration failed");
        }
        outboxEventRepository.save(event);
        return new AuthResponse(jwtUtils.generateToken(createdSeller));
    }

    public AuthResponse login(LoginDto loginDto) {
        var foundedAccount= accountRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"There is no registered account with this email."));
        if(!passwordSecurity.checkPassword(loginDto.getPassword(),foundedAccount.getEncodedPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid password");
        }
        String token=jwtUtils.generateToken(foundedAccount);
        return new AuthResponse(token);
    }
    @Transactional
    @Scheduled(fixedRate = 5000)
    public void processOutboxEvents(){
        List<OutboxEvent> events=outboxEventRepository.findByProcessedFalse();
        for(OutboxEvent event : events){
            try{
                kafkaTemplate.send(event.getEventType(),event.getAggregateId(),event.getPayload())
                        .get(5, TimeUnit.SECONDS);
                outboxEventRepository.markAsProcessed(event.getId());
                outboxEventRepository.flush();
            }catch (Exception e){
                System.out.println("Failed to send event: "+e.getMessage());
            }
        }
    }
}
