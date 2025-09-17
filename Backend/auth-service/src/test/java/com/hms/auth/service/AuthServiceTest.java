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
        user.setName("john");
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
        user.setName("john");
        user.setRole("ADMIN");

        when(repository.findByName("john")).thenReturn(Optional.of(user));

        String role = authService.getUserRole("john");

        assertEquals("ADMIN", role);
    }

    @Test
    void testGetUserRole_UserNotFound() {
        when(repository.findByName("jane")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.getUserRole("jane"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGenerateToken() {
        when(jwtService.generateToken("john", "ADMIN")).thenReturn("fakeToken");

        String token = authService.generateToken("john", "ADMIN");

        assertEquals("fakeToken", token);
        verify(jwtService, times(1)).generateToken("john", "ADMIN");
    }

    @Test
    void testValidateToken() {
        doNothing().when(jwtService).validateToken("someToken");

        authService.validateToken("someToken");

        verify(jwtService, times(1)).validateToken("someToken");
    }
}
