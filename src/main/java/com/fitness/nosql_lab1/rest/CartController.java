package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.objects.CartItem;
import com.fitness.nosql_lab1.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItem(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody CartItem cartItem
            ) throws Exception {
        return cartService.addItem(session, cartItem);
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        return cartService.getCart(session);
    }

    @GetMapping("/delete/{productId}")
    public ResponseEntity<String> deleteItem(
            @CookieValue(name = "SESSION", required = false) String session,
            @PathVariable String productId
    ) throws Exception {
        return cartService.deleteItem(session, productId);
    }

    @GetMapping("/clear")
    public ResponseEntity<String> clearCart(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        return cartService.clearCart(session);
    }
}
