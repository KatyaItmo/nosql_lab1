package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.ProductDto;
import com.fitness.nosql_lab1.objects.Product;
import com.fitness.nosql_lab1.services.CheckUserService;
import com.fitness.nosql_lab1.services.EtcdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final CheckUserService checkUserService;

    public ProductController(EtcdService etcdService, ObjectMapper objectMapper, CheckUserService checkUserService) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.checkUserService = checkUserService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> jsonList = etcdService.getList("product:");
        List<Product> productList = new ArrayList<>();

        for (String json : jsonList) {
            productList.add(objectMapper.readValue(json, Product.class));
        }

        return ResponseEntity.ok(productList);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody ProductDto productDto
    ) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String productID = UUID.randomUUID().toString();

        Product product = new Product(
                productID,
                productDto.getName(),
                productDto.getDescription(),
                productDto.getCost()
        );

        String productJson = objectMapper.writeValueAsString(product);
        etcdService.put("product:" + productID, productJson);

        return ResponseEntity.ok("Добавлен новый товар " + productID);
    }
}