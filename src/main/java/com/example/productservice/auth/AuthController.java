package com.example.productservice.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RestClient authClient;

    public AuthController(
            RestClient.Builder restClientBuilder,
            @Value("${auth.service.url:http://localhost:8082}") String authServiceUrl) {
        this.authClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String encodedPassword = Base64.getEncoder()
                .encodeToString(request.password().getBytes(StandardCharsets.UTF_8));
        Bth2LoginRequest bth2Request = new Bth2LoginRequest(request.userName(), encodedPassword);

        try {
            LoginResponse response = authClient.post()
                    .uri("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bth2Request)
                    .retrieve()
                    .body(LoginResponse.class);
            if (response == null || response.token() == null || response.token().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatusCode.valueOf(502),
                        "Dịch vụ xác thực trả về phản hồi không hợp lệ");
            }
            return ResponseEntity.ok(response);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 401) {
                return ResponseEntity.status(exception.getStatusCode())
                        .body(new LoginErrorResponse("Tên đăng nhập hoặc mật khẩu không đúng"));
            }
            return ResponseEntity.status(HttpStatusCode.valueOf(502))
                    .body(new LoginErrorResponse(
                            "Dịch vụ tại AUTH_SERVICE_URL không nhận endpoint POST /api/v1/auth/login (HTTP "
                                    + exception.getStatusCode().value() + "). Hãy kiểm tra đúng ứng dụng Bth2 đang chạy."));
        } catch (ResourceAccessException exception) {
            return ResponseEntity.status(HttpStatusCode.valueOf(503))
                    .body(new LoginErrorResponse(
                            "Không kết nối được dịch vụ xác thực Bth2. Hãy chạy Bth2 trên cổng 8082 hoặc cấu hình AUTH_SERVICE_URL."));
        }
    }

    private record Bth2LoginRequest(String username, String password) {
    }
}
