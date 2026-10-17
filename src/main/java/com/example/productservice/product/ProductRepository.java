package com.example.productservice.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByOrderByIdAsc();
}
