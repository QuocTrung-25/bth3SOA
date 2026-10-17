package com.example.productservice.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserAuthService userAuthService;

    @Test
    void authenticatesBase64PasswordFromDump() {
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), any(Object[].class)))
                .thenReturn("MTIzNDU2");

        assertTrue(userAuthService.authenticate("Admin", "123456"));
    }

    @Test
    void authenticatesPlainTextPassword() {
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), any(Object[].class)))
                .thenReturn("123456");

        assertTrue(userAuthService.authenticate("Admin", "123456"));
    }

    @Test
    void rejectsUnknownUserOrWrongPassword() {
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), any(Object[].class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        assertFalse(userAuthService.authenticate("unknown", "123456"));
    }
}
