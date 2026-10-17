package com.example.productservice.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {
    private static final String FIND_PASSWORD_SQL =
            "SELECT `Password` FROM `user` WHERE BINARY `UserName` = BINARY ? LIMIT 1";

    private final JdbcTemplate jdbcTemplate;

    public UserAuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean authenticate(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null) {
            return false;
        }

        try {
            String storedPassword = jdbcTemplate.queryForObject(
                    FIND_PASSWORD_SQL, String.class, userName);
            String encodedPassword = Base64.getEncoder()
                    .encodeToString(password.getBytes(StandardCharsets.UTF_8));
            return storedPassword != null
                    && (storedPassword.equals(password) || storedPassword.equals(encodedPassword));
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }
}
