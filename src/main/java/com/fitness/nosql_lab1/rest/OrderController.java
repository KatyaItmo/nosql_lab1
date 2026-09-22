package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.DecisionDto;
import com.fitness.nosql_lab1.dtos.OrderDto;
import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.services.EtcdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;

    public OrderController(EtcdService etcdService, ObjectMapper objectMapper) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderDto orderDto) throws Exception {
        String orderID = UUID.randomUUID().toString();

        Order order = new Order(orderID,
                orderDto.getClientName(),
                orderDto.getSubscriptionType(),
                orderDto.getPeriod(),
                orderDto.getAmount()
        );

        String orderJson = objectMapper.writeValueAsString(order);
        etcdService.put("order:pending:" + orderID, orderJson);
        return ResponseEntity.ok("Заказ создан. Номер заказа: " + orderID);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptOrder(@RequestBody DecisionDto decisionObject) throws Exception {
        String orderID = decisionObject.getOrderID();
        String decision = decisionObject.getDecision();

        if (decision.equals("Принять")) {
            Order order = objectMapper.readValue(etcdService.get(orderID), Order.class);

            String orderJson = objectMapper.writeValueAsString(order);
            etcdService.delete("order:pending:" + orderID);
            etcdService.put("order:confirmed:" + orderID, orderJson);
            return ResponseEntity.ok("Принят заказ " + orderID);

        } else {
            etcdService.delete("order:pending:" + orderID);
            return ResponseEntity.ok("Отклонен заказ " + orderID);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Order>> getPendingOrders() throws Exception {
        List<String> jsonList = etcdService.getList("order:pending");

        List<Order> ordersList = jsonList.stream()
                .map(json -> {
                    return objectMapper.readValue(json, Order.class);
                })
                .toList();

        return ResponseEntity.ok(ordersList);
    }
}
