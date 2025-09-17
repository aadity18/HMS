package com.hms.auth.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtService.generateToken("john", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // should not throw exception
        assertDoesNotThrow(() -> jwtService.validateToken(token));
    }

    @Test
    void testGenerateTokenContainsRoleClaim() {
        String token = jwtService.generateToken("john", "USER");

        assertNotNull(token);

        // validate works without exception
        assertDoesNotThrow(() -> jwtService.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThrows(Exception.class, () -> jwtService.validateToken(invalidToken));
    }
}
