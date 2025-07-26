package com.example.raspberriesAuthService.controller;

import com.example.raspberriesAuthService.config.SecurityConfig;
import com.example.raspberriesAuthService.dto.LoginDto;
import com.example.raspberriesAuthService.dto.RegisterDto;
import com.example.raspberriesAuthService.dto.AuthResponse;
import com.example.raspberriesAuthService.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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

    private static final String registerUserPath="/api/auth/register/user";
    private static final String registerCompanyPath="/api/auth/register/company";
    private static final String loginPath="/api/auth/login";
    @Test
    void register_success() throws Exception {
        RegisterDto registerDto = new RegisterDto(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD,"USER");
        AuthResponse expectedResponse = new AuthResponse("test_token");
        when(authService.registerUser(registerDto)).thenReturn(expectedResponse);
        mockMvc.perform(post(registerUserPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("@.accessToken",is("test_token")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","use","1","aaaaaaaaaaaaaaaaa"})
    void register_incorrectName(String incorrectName) throws Exception {
        RegisterDto registerDto = new RegisterDto(incorrectName, VALID_EMAIL, VALID_PASSWORD,"USER");
        mockMvc.perform(post(registerUserPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Name length from 4 to 16 characters")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","test@", "@test.com", "test@.com", "test@com","a@a.a","a@.aa"})
    void register_incorrectEmail(String incorrectEmail) throws Exception {
        RegisterDto registerDto = new RegisterDto(VALID_USERNAME, incorrectEmail, VALID_PASSWORD,"USER");
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Incorrect email address")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","123","1","1111111111111111111111111111111111"})
    void register_incorrectPassword(String incorrectPassword) throws Exception {
        RegisterDto registerDto = new RegisterDto(VALID_USERNAME, VALID_EMAIL, incorrectPassword,"USER");
        mockMvc.perform(post(registerUserPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Password length from 8 to 32 characters")));
    }

    @Test
    void login_success() throws Exception {
        LoginDto loginDto = new LoginDto(VALID_EMAIL, VALID_PASSWORD);
        AuthResponse expectedResponse=new AuthResponse("test_token");
        when(authService.login(loginDto)).thenReturn(expectedResponse);
        mockMvc.perform(post(loginPath)
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
        mockMvc.perform(post(loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Incorrect email address")));
    }
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings={"","123","1","1111111111111111111111111111111111"})
    void login_incorrectPassword(String incorrectPassword) throws Exception {
        LoginDto loginDto = new LoginDto(VALID_EMAIL, incorrectPassword);
        mockMvc.perform(post(loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("@.reason",is("Password length from 8 to 32 characters")));
    }
}
