package com.example.productservice.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
