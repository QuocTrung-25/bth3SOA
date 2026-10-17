package com.example.productservice.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String userName,
        @NotBlank String password) {
}
