package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.DecisionDto;
import com.fitness.nosql_lab1.dtos.OrderDto;
import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.objects.Plan;
import com.fitness.nosql_lab1.objects.Status;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.services.CheckUserService;
import com.fitness.nosql_lab1.services.EtcdService;
import org.springframework.http.HttpStatus;
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
    private final CheckUserService checkUserService;

    public OrderController(EtcdService etcdService, ObjectMapper objectMapper, CheckUserService checkUserService) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.checkUserService = checkUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@CookieValue(name = "SESSION") String session, @RequestBody OrderDto orderDto) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещен");
        }

        String orderID = UUID.randomUUID().toString();
        Status status = Status.NEW;

        String jsonUser = etcdService.get("session:" + session);
        User user = objectMapper.readValue(jsonUser, User.class);

        Order order = new Order(orderID,
                status,
                orderDto.getClientName(),
                user.getUsername(),
                orderDto.getSubscriptionType(),
                orderDto.getPeriod(),
                orderDto.getAmount()
        );

        String orderJson = objectMapper.writeValueAsString(order);
        etcdService.put("order:new:" + orderID, orderJson);
        return ResponseEntity.ok("Заказ создан. Номер заказа: " + orderID);
    }


    @GetMapping("/userOrders")
    public ResponseEntity<List<Order>> getUserOrders(@CookieValue(name = "SESSION") String session) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String jsonUser = etcdService.get("session:" + session);
        User currentUser = objectMapper.readValue(jsonUser, User.class);

        List<String> jsonList = etcdService.getList("orders:");

        List<Order> ordersList = jsonList.stream()
                .map(json -> {
                    return objectMapper.readValue(json, Order.class);
                })
                .filter(order -> currentUser.getUsername().equals(order.getUsername()))
                .toList();

        return ResponseEntity.ok(ordersList);
    }

    //старая версия переделать
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

    //старая версия переделать
    @GetMapping("/newOrders")
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
