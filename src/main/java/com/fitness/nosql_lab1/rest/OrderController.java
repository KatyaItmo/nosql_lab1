package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.ModerationDto;
import com.fitness.nosql_lab1.dtos.OrderDto;
import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.objects.User;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final CheckUserService checkUserService;

    public OrderController(EtcdService etcdService, ObjectMapper objectMapper, CheckUserService checkUserService) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.checkUserService = checkUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody OrderDto orderDto
    ) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещен");
        }

        String orderID = UUID.randomUUID().toString();
        String status = "new";

        String jsonUser = etcdService.get("session:" + session);
        User user = objectMapper.readValue(jsonUser, User.class);

        Order order = new Order(
                orderID,
                status,
                orderDto.getClientName(),
                orderDto.getIdProduct(),
                orderDto.getAmount()
        );

        String orderJson = objectMapper.writeValueAsString(order);
        etcdService.put("order:" + user.getUsername() + ":" + orderID, orderJson);

        return ResponseEntity.ok("Заказ создан. Номер заказа: " + orderID);
    }

    @GetMapping("/userOrders")
    public ResponseEntity<List<Order>> getUserOrders(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String jsonUser = etcdService.get("session:" + session);
        User user = objectMapper.readValue(jsonUser, User.class);

        List<String> jsonList = etcdService.getList("order:" + user.getUsername() + ":");
        List<Order> ordersList = new ArrayList<>();

        for (String json : jsonList) {
            ordersList.add(objectMapper.readValue(json, Order.class));
        }

        return ResponseEntity.ok(ordersList);
    }

    @GetMapping("/allOrders")
    public ResponseEntity<List<Order>> getAllOrders(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> jsonList = etcdService.getList("order:");
        List<Order> ordersList = new ArrayList<>();

        for (String json : jsonList) {
            ordersList.add(objectMapper.readValue(json, Order.class));
        }

        return ResponseEntity.ok(ordersList);
    }

    @PostMapping("/moderate")
    public ResponseEntity<String> moderateOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody ModerationDto moderationDto
    ) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String orderKey = "order:" + moderationDto.getUsername() + ":" + moderationDto.getOrderID();
        String orderJson = etcdService.get(orderKey);

        if (orderJson == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказ не найден");
        }

        Order order = objectMapper.readValue(orderJson, Order.class);
        order.setStatus(moderationDto.getNewStatus());

        etcdService.put(orderKey, objectMapper.writeValueAsString(order));

        return ResponseEntity.ok("Статус заказа " + order.getOrderID() + " успешно изменен");
    }
}