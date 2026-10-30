package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.CartItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;

    public CartService(EtcdService etcdService, ObjectMapper objectMapper, SessionService sessionService) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
    }

    public ResponseEntity<String> addItem(String sessionToken, CartItem item) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("работа с корзиной запрещена");
        }

        String productId = item.getProduct().getId();

        String jsonMap = etcdService.get("cart:" + sessionToken);
        Map<String, CartItem> cart = jsonMap == null ? new HashMap<>() : objectMapper.readValue(jsonMap, new TypeReference<Map<String, CartItem>>() {});

        if (cart.get(productId) != null) {
            item.setAmount(item.getAmount() + cart.get(productId).getAmount());
        }
        cart.put(productId, item);

        jsonMap = objectMapper.writeValueAsString(cart);
        etcdService.putTemporary("cart:" + sessionToken, jsonMap, 1800);

        return ResponseEntity.ok("товар " + item.getProduct().getName() + " добавлен в корзину");
    }


    public ResponseEntity<String> deleteItem(String sessionToken, String productId) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("работа с корзиной запрещена");
        }

        String jsonMap = etcdService.get("cart:" + sessionToken);
        Map<String, CartItem> cart = jsonMap == null ? new HashMap<>() : objectMapper.readValue(jsonMap, new TypeReference<Map<String, CartItem>>() {});

        cart.remove(productId);

        jsonMap = objectMapper.writeValueAsString(cart);
        etcdService.putTemporary("cart:" + sessionToken, jsonMap, 1800);

        return ResponseEntity.ok("товар удален из корзины");
    }


    public ResponseEntity<String> clearCart(String sessionToken) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("работа с корзиной запрещена");
        }

        delete(sessionToken);

        return ResponseEntity.ok("корзина очищена");
    }


    public ResponseEntity<List<CartItem>> getCart(String sessionToken) throws Exception {
        List<CartItem> listCart = getList(sessionToken);

        if (listCart == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(listCart);
    }


    public void delete(String sessionToken) throws Exception {
        String jsonMap = objectMapper.writeValueAsString(new HashMap<>());
        etcdService.putTemporary("cart:" + sessionToken, jsonMap, 1800);
    }


    public List<CartItem> getList(String sessionToken) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            return null;
        }

        String jsonMap = etcdService.get("cart:" + sessionToken);
        Map<String, CartItem> cart = jsonMap == null ? new HashMap<>() : objectMapper.readValue(jsonMap, new TypeReference<Map<String, CartItem>>() {});

        return new ArrayList<>(cart.values());
    }
}
