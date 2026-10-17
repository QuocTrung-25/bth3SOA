package com.example.productservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private final JwtService jwtService = new JwtService(
            "ThisIsASecretKeyForJwtLab2026VeryLong123456789", 60 * 60 * 1000);

    @Test
    void validatesTokenAndExtractsUsername() {
        String token = jwtService.generateToken("Admin");

        assertTrue(jwtService.isTokenValid(token));
        assertEquals("Admin", jwtService.extractUserName(token));
        assertFalse(jwtService.isTokenValid("not-a-jwt"));
    }
}
