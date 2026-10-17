package com.example.productservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtAuthenticationFilterTest {
    private final JwtService jwtService = mock(JwtService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

    @Test
    void blocksProductRequestsWithoutToken() throws ServletException, IOException {
        MockHttpServletRequest request = productRequest(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        verify(chain, org.mockito.Mockito.never()).doFilter(request, response);
    }

    @Test
    void letsValidBearerTokenReachProductEndpoint() throws ServletException, IOException {
        MockHttpServletRequest request = productRequest("Bearer signed-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(jwtService.isTokenValid("signed-jwt")).thenReturn(true);
        when(jwtService.extractUserName("signed-jwt")).thenReturn("Admin");

        filter.doFilter(request, response, chain);

        assertEquals("Admin", request.getAttribute("userName"));
        verify(chain).doFilter(request, response);
    }

    private MockHttpServletRequest productRequest(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/products");
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        return request;
    }
}
