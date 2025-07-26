package com.example.raspberriesAuthService.service;

import com.example.raspberriesAuthService.dto.LoginDto;
import com.example.raspberriesAuthService.dto.RegisterDto;
import com.example.raspberriesAuthService.dto.AuthResponse;
import com.example.raspberriesAuthService.model.User;
import com.example.raspberriesAuthService.util.JwtUtils;
import com.example.raspberriesAuthService.util.PasswordSecurity;
import com.example.raspberriesAuthService.model.Account;
import com.example.raspberriesAuthService.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordSecurity passwordSecurity;
    @InjectMocks
    private AuthService authService;
    private static final String VALID_USERNAME = "user";
    private static final String VALID_EMAIL = "test@test.test";
    private static final String VALID_PASSWORD = "password";
    @Test
    void register_success(){
        RegisterDto registerDto=new RegisterDto(VALID_USERNAME,VALID_EMAIL,VALID_PASSWORD,"USER");
        User newUser=new User(VALID_EMAIL,VALID_PASSWORD);
        newUser.setId(1L);

        when(accountRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(passwordSecurity.encodePassword(registerDto.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any())).thenReturn(newUser);
        when(jwtUtils.generateToken(newUser)).thenReturn("test-token");
        AuthResponse result = authService.registerUser(registerDto);

        assertNotNull(result);
        assertEquals("test-token", result.getAccessToken());
        verify(accountRepository).findByEmail(VALID_EMAIL);
        verify(passwordSecurity).encodePassword(VALID_PASSWORD);
        verify(accountRepository).save(argThat(user -> user.getEncodedPassword().equals("encodedPassword")));
        verify(jwtUtils).generateToken(newUser);
    }
    @Test
    void register_conflictEmail(){
        RegisterDto registerDto=new RegisterDto(VALID_USERNAME,VALID_EMAIL,VALID_PASSWORD,"USER");
        when(accountRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(new User()));
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,()->authService.registerUser(registerDto));
        assertEquals(HttpStatus.CONFLICT,exception.getStatusCode());
        assertEquals("Email is already in use",exception.getReason());
        verify(accountRepository).findByEmail(registerDto.getEmail());
    }

    @Test
    void login_success(){
        LoginDto loginDto=new LoginDto(VALID_EMAIL,VALID_PASSWORD);
        User user=new User(VALID_EMAIL,VALID_PASSWORD);
        user.setId(1L);
        when(accountRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordSecurity.checkPassword(loginDto.getPassword(),user.getEncodedPassword())).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("test-token");
        var result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("test-token", result.getAccessToken());
        verify(accountRepository).findByEmail(loginDto.getEmail());
        verify(passwordSecurity).checkPassword(loginDto.getPassword(),user.getEncodedPassword());
        verify(jwtUtils).generateToken(user);
    }
    @Test
    void login_EmailNotFound(){
        LoginDto loginDto=new LoginDto(VALID_EMAIL,VALID_PASSWORD);
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,()->authService.login(loginDto));

        assertEquals(HttpStatus.UNAUTHORIZED,exception.getStatusCode());
        assertEquals("There is no registered account with this email.",exception.getReason());
        verify(accountRepository).findByEmail(loginDto.getEmail());
    }
    @Test
    void login_IncorrectPassword(){
        LoginDto loginDto=new LoginDto(VALID_EMAIL,VALID_PASSWORD);
        User user=new User(VALID_EMAIL,VALID_PASSWORD);
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordSecurity.checkPassword(anyString(),anyString())).thenReturn(false);
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,()->authService.login(loginDto));

        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatusCode());
        assertEquals("Invalid password",exception.getReason());
        verify(accountRepository).findByEmail(anyString());
        verify(passwordSecurity).checkPassword(anyString(),anyString());
    }
}
