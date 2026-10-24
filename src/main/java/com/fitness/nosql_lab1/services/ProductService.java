package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.Product;
import com.fitness.nosql_lab1.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void putProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProduct(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
