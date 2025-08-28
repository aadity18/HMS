package com.example.auth_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hms.auth.model.UserCredential;
import com.hms.auth.repository.UserCredentialRepository;
import com.hms.auth.service.AuthService;
import com.hms.auth.service.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserCredential testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserCredential();
        testUser.setId(1);
        testUser.setName("amit");
        testUser.setPassword("password");
        testUser.setRole("OWNER");
    }

    @Test
    void testSaveUser_shouldEncodePasswordAndSave() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        String result = authService.saveUser(testUser);

        assertEquals("user added to the system", result);
        assertEquals("encodedPassword", testUser.getPassword());
        verify(repository, times(1)).save(testUser);
    }

    @Test
    void testGetUserRole_shouldReturnUserRole() {
        when(repository.findByName("amit")).thenReturn(Optional.of(testUser));

        String role = authService.getUserRole("amit");

        assertEquals("OWNER", role);
        verify(repository).findByName("amit");
    }

    @Test
    void testGetUserRole_userNotFound_shouldThrowException() {
        when(repository.findByName("notfound")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.getUserRole("notfound"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGenerateToken_shouldReturnToken() {
        when(jwtService.generateToken("amit", "OWNER")).thenReturn("mockToken");

        String token = authService.generateToken("amit", "OWNER");

        assertEquals("mockToken", token);
        verify(jwtService).generateToken("amit", "OWNER");
    }

    @Test
    void testValidateToken_shouldCallJwtService() {
        authService.validateToken("mockToken");

        verify(jwtService).validateToken("mockToken");
    }
}
