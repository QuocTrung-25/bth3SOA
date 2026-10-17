package com.example.productservice.product;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAllByOrderByIdAsc().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Integer id) {
        return ProductResponse.from(findProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.quantity());
        return ProductResponse.from(productRepository.saveAndFlush(product));
    }

    public ProductResponse update(Integer id, ProductRequest request) {
        Product product = findProduct(id);
        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.quantity());
        return ProductResponse.from(productRepository.saveAndFlush(product));
    }

    public void delete(Integer id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sản phẩm có id " + id));
    }
}
