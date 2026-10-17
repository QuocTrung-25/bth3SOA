package com.example.productservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private UserAuthService userAuthService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void issuesBearerTokenForValidCredentials() {
        when(userAuthService.authenticate("Admin", "123456")).thenReturn(true);
        when(jwtService.generateToken("Admin")).thenReturn("signed-jwt");

        LoginResponse response = authController.login(new LoginRequest("Admin", "123456"));

        assertEquals("signed-jwt", response.token());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void rejectsInvalidCredentials() {
        when(userAuthService.authenticate("Admin", "wrong")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authController.login(new LoginRequest("Admin", "wrong")));

        assertEquals(401, exception.getStatusCode().value());
    }
}
