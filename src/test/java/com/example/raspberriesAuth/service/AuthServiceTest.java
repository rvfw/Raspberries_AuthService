package com.example.raspberriesAuth.service;

import com.example.raspberriesAuth.dto.LoginDto;
import com.example.raspberriesAuth.dto.RegisterDto;
import com.example.raspberriesAuth.dto.AuthResponse;
import com.example.raspberriesAuth.util.JwtUtils;
import com.example.raspberriesAuth.util.PasswordSecurity;
import com.example.raspberriesAuth.model.User;
import com.example.raspberriesAuth.repository.UserRepository;
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
    private UserRepository userRepository;
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
        RegisterDto registerDto=new RegisterDto(VALID_USERNAME,VALID_EMAIL,VALID_PASSWORD);
        User newUser=new User(VALID_EMAIL,VALID_PASSWORD);
        newUser.setId(1L);

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(passwordSecurity.encodePassword(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(newUser);
        when(jwtUtils.generateToken(newUser)).thenReturn("test-token");
        AuthResponse result = authService.register(registerDto);

        assertNotNull(result);
        assertEquals("test-token", result.getAccessToken());
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordSecurity).encodePassword(VALID_PASSWORD);
        verify(userRepository).save(argThat(user -> user.getEncodedPassword().equals("encodedPassword")));
        verify(jwtUtils).generateToken(newUser);
    }
    @Test
    void register_conflictEmail(){
        RegisterDto registerDto=new RegisterDto(VALID_USERNAME,VALID_EMAIL,VALID_PASSWORD);
        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(new User()));
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,()->authService.register(registerDto));
        assertEquals(HttpStatus.CONFLICT,exception.getStatusCode());
        assertEquals("Email is already in use",exception.getReason());
        verify(userRepository).findByEmail(registerDto.getEmail());
    }

    @Test
    void login_success(){
        LoginDto loginDto=new LoginDto(VALID_EMAIL,VALID_PASSWORD);
        User user=new User(VALID_EMAIL,VALID_PASSWORD);
        user.setId(1L);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordSecurity.checkPassword(loginDto.getPassword(),user.getEncodedPassword())).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("test-token");
        var result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("test-token", result.getAccessToken());
        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(passwordSecurity).checkPassword(loginDto.getPassword(),user.getEncodedPassword());
        verify(jwtUtils).generateToken(user);
    }
    @Test
    void login_EmailNotFound(){
        LoginDto loginDto=new LoginDto(VALID_EMAIL,VALID_PASSWORD);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,()->authService.login(loginDto));

        assertEquals(HttpStatus.UNAUTHORIZED,exception.getStatusCode());
        assertEquals("There is no registered account with this email.",exception.getReason());
        verify(userRepository).findByEmail(loginDto.getEmail());
    }
    @Test
    void login_IncorrectPassword(){
        LoginDto loginDto=new LoginDto(VALID_EMAIL,VALID_PASSWORD);
        User user=new User(VALID_EMAIL,VALID_PASSWORD);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordSecurity.checkPassword(anyString(),anyString())).thenReturn(false);
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,()->authService.login(loginDto));

        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatusCode());
        assertEquals("Invalid password",exception.getReason());
        verify(userRepository).findByEmail(anyString());
        verify(passwordSecurity).checkPassword(anyString(),anyString());
    }
}
