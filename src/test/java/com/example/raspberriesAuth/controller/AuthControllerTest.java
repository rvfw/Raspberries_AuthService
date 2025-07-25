package com.example.raspberriesAuth.controller;

import com.example.raspberriesAuth.config.SecurityConfig;
import com.example.raspberriesAuth.dto.LoginDto;
import com.example.raspberriesAuth.dto.RegisterDto;
import com.example.raspberriesAuth.dto.AuthResponse;
import com.example.raspberriesAuth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String VALID_USERNAME="user";
    private static final String VALID_EMAIL="test@test.test";
    private static final String VALID_PASSWORD="password";
    @Test
    void register_success() throws Exception {
        RegisterDto registerDto = new RegisterDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        AuthResponse expectedResponse = new AuthResponse("test_token");
        when(authService.register(registerDto)).thenReturn(expectedResponse);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("@.accessToken",is("test_token")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","use","1","aaaaaaaaaaaaaaaaa"})
    void register_incorrectName(String incorrectName) throws Exception {
        RegisterDto registerDto = new RegisterDto(incorrectName, VALID_EMAIL, VALID_PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Name length from 4 to 16 characters")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","test@", "@test.com", "test@.com", "test@com","a@a.a","a@.aa"})
    void register_incorrectEmail(String incorrectEmail) throws Exception {
        RegisterDto registerDto = new RegisterDto(VALID_USERNAME, incorrectEmail, VALID_PASSWORD);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Incorrect email address")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","123","1","11111111111111111"})
    void register_incorrectPassword(String incorrectPassword) throws Exception {
        RegisterDto registerDto = new RegisterDto(VALID_USERNAME, VALID_EMAIL, incorrectPassword);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Password length from 4 to 16 characters")));
    }
    @Test
    void login_success() throws Exception {
        LoginDto loginDto = new LoginDto(VALID_EMAIL, VALID_PASSWORD);
        AuthResponse expectedResponse=new AuthResponse("test_token");
        when(authService.login(loginDto)).thenReturn(expectedResponse);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.accessToken",is("test_token")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","test@", "@test.com", "test@.com", "test@com","a@a.a","a@.aa"})
    void login_incorrectEmail(String incorrectEmail) throws Exception {
        LoginDto loginDto = new LoginDto(incorrectEmail, VALID_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Incorrect email address")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","123","1","11111111111111111"})
    void login_incorrectPassword(String incorrectPassword) throws Exception {
        LoginDto loginDto = new LoginDto(VALID_EMAIL, incorrectPassword);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Password length from 4 to 16 characters")));
    }
}
