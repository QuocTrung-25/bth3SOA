package com.example.productservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class AuthControllerTest {
    @Test
    void sendsBase64PasswordToBth2AndReturnsJwt() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AuthController controller = new AuthController(builder, "http://localhost:8080");

        server.expect(requestTo("http://localhost:8080/api/v1/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"username":"admin","password":"MTIzNDU2"}
                        """))
                .andRespond(withSuccess(
                        "{\"token\":\"demo-jwt\",\"tokenType\":\"Bearer\"}",
                        MediaType.APPLICATION_JSON));

        var response = controller.login(new LoginRequest("admin", "123456"));

        assertEquals(200, response.getStatusCode().value());
        LoginResponse result = (LoginResponse) response.getBody();
        assertEquals("demo-jwt", result.token());
        assertEquals("Bearer", result.tokenType());
        server.verify();
    }

    @Test
    void reportsMisconfiguredBth2Endpoint() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AuthController controller = new AuthController(builder, "http://localhost:8080");
        server.expect(requestTo("http://localhost:8080/api/v1/auth/login"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var response = controller.login(new LoginRequest("admin", "123456"));

        assertEquals(502, response.getStatusCode().value());
        LoginErrorResponse error = (LoginErrorResponse) response.getBody();
        assertTrue(error.message().contains("AUTH_SERVICE_URL"));
        server.verify();
    }
}
