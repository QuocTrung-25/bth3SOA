package com.example.productservice.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createsProductFromRequest() {
        ProductRequest request = new ProductRequest(
                "Bàn phím", "Bluetooth", new BigDecimal("1250000.00"), 4);
        when(productRepository.saveAndFlush(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.create(request);

        assertEquals(request.name(), response.name());
        assertEquals(request.description(), response.description());
        assertEquals(request.price(), response.price());
        assertEquals(request.quantity(), response.quantity());
    }

    @Test
    void updatesExistingProduct() {
        Product product = new Product("Cũ", null, new BigDecimal("100.00"), 1);
        ProductRequest request = new ProductRequest(
                "Mới", "Mô tả", new BigDecimal("200.00"), 3);
        when(productRepository.findById(7)).thenReturn(Optional.of(product));
        when(productRepository.saveAndFlush(product)).thenReturn(product);

        ProductResponse response = productService.update(7, request);

        assertEquals(request.name(), response.name());
        assertEquals(request.description(), response.description());
        assertEquals(request.price(), response.price());
        assertEquals(request.quantity(), response.quantity());
    }

    @Test
    void returnsNotFoundWhenProductDoesNotExist() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.getById(99));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void deletesExistingProduct() {
        Product product = new Product("Bàn phím", null, new BigDecimal("100.00"), 1);
        when(productRepository.findById(7)).thenReturn(Optional.of(product));

        productService.delete(7);

        verify(productRepository).delete(product);
    }
}
