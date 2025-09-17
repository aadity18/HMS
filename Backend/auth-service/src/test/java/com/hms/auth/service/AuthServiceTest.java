package com.hms.auth.service;

import com.hms.auth.model.UserCredential;
import com.hms.auth.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        UserCredential user = new UserCredential();
        user.setEmail("john@example.com");
        user.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(repository.save(any(UserCredential.class))).thenReturn(user);

        String result = authService.saveUser(user);

        assertEquals("user added to the system", result);
        assertEquals("encodedPass", user.getPassword());
        verify(repository, times(1)).save(user);
    }

    @Test
    void testGetUserRole_Success() {
        UserCredential user = new UserCredential();
        user.setEmail("john@example.com");
        user.setRole("ADMIN");

        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        String role = authService.getUserRole("john@example.com");

        assertEquals("ADMIN", role);
    }

    @Test
    void testGetUserRole_UserNotFound() {
        when(repository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.getUserRole("jane@example.com"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGenerateToken() {
        when(jwtService.generateToken("john@example.com", "ADMIN")).thenReturn("fakeToken");

        String token = authService.generateToken("john@example.com", "ADMIN");

        assertEquals("fakeToken", token);
        verify(jwtService, times(1)).generateToken("john@example.com", "ADMIN");
    }

    @Test
    void testValidateToken() {
        doNothing().when(jwtService).validateToken("someToken");

        authService.validateToken("someToken");

        verify(jwtService, times(1)).validateToken("someToken");
    }
}
