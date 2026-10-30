package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.Code;
import com.fitness.nosql_lab1.dtos.Confirmation;
import com.fitness.nosql_lab1.dtos.Moderation;
import com.fitness.nosql_lab1.dtos.OrderDto;
import com.fitness.nosql_lab1.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Code> createOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody OrderDto orderDto
    ) throws Exception {
        return orderService.createOrder(session, orderDto);
    }

    @PostMapping("/repeat/{orderId}")
    public ResponseEntity<Code> repeatOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @PathVariable String orderId
    ) throws Exception {
        return orderService.repeatOrder(session, orderId);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody Confirmation confirmation
    ) throws Exception {
        return orderService.confirmOrder(session, confirmation);
    }

    @PostMapping("/moderate")
    public ResponseEntity<String> moderateOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody Moderation moderation
    ) throws Exception {
        return orderService.moderateOrder(session, moderation);
    }

    @GetMapping("/allOrders")
    public ResponseEntity<List<OrderDto>> getAllOrders(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        return orderService.getAllOrders(session);
    }

    @GetMapping("/userOrders")
    public ResponseEntity<List<OrderDto>> getUserOrders(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        return orderService.getUserOrders(session);
    }
}