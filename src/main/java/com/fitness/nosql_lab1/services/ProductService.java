package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.Product;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final MessageService messageService;

    public ProductService(ProductRepository productRepository, EtcdService etcdService, ObjectMapper objectMapper, SessionService sessionService, MessageService messageService) {
        this.productRepository = productRepository;
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    public ResponseEntity<String> createProduct(String sessionToken, Product product) throws Exception {
        if (!sessionService.checkUser(sessionToken, "moder")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("вы не можете добавлять товары");
        }

        String id = UUID.randomUUID().toString();
        product.setId(id);

        productRepository.save(product);
        etcdService.delete("cache:products");

        User moder = sessionService.getUser(sessionToken);

        messageService.makeNewsletter("moder", "НОВЫЙ ТОВАР", "модератор " + moder + " добавил новый товар " + product.getId());
        messageService.makeNewsletter("user", "НОВЫЙ ТОВАР", "в каталоге доступен новый товар - " + product.getName());

        return ResponseEntity.ok("добавлен новый товар " + id);
    }


    public ResponseEntity<List<Product>> getProducts() throws Exception {
        String productsJson = etcdService.get("cache:products");
        List<Product> products;

        if (productsJson == null) {
            products = productRepository.findAll();
            productsJson = objectMapper.writeValueAsString(products);
            etcdService.put("cache:products", productsJson);
        } else {
            products = objectMapper.readValue(productsJson, new TypeReference<List<Product>>() {});
        }

        return ResponseEntity.ok(products);
    }
}
