package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.ProductRequestDto;
import com.fitness.nosql_lab1.objects.Product;
import com.fitness.nosql_lab1.services.CheckUserService;
import com.fitness.nosql_lab1.services.EtcdService;
import com.fitness.nosql_lab1.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final CheckUserService checkUserService;

    public ProductController(ProductService productService, EtcdService etcdService, ObjectMapper objectMapper, CheckUserService checkUserService) {
        this.productService = productService;
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.checkUserService = checkUserService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {

        String productsJson = etcdService.get("cache:products");
        List<Product> products;
        if (productsJson == null) {
            products = productService.getAllProducts();

            productsJson = objectMapper.writeValueAsString(products);
            etcdService.put("cache:products", productsJson);
        } else {
            products = objectMapper.readValue(productsJson, new TypeReference<List<Product>>() {});
        }

        return ResponseEntity.ok(products);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody ProductRequestDto productRequestDto
    ) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String idProduct = UUID.randomUUID().toString();

        Product product = new Product(
                idProduct,
                productRequestDto.getName(),
                productRequestDto.getDescription(),
                productRequestDto.getPrice()
        );

        productService.putProduct(product);
        etcdService.delete("cache:products");

        return ResponseEntity.ok("добавлен новый товар " + idProduct);
    }
}