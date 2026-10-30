package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.objects.Product;
import com.fitness.nosql_lab1.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        return productService.getProducts();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody Product product
    ) throws Exception {
        return productService.createProduct(session, product);
    }
}