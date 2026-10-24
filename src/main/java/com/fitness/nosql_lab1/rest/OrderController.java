package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.*;
import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.services.CheckUserService;
import com.fitness.nosql_lab1.services.EtcdService;
import com.fitness.nosql_lab1.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.Random;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final CheckUserService checkUserService;

    public OrderController(OrderService orderService, EtcdService etcdService, ObjectMapper objectMapper, CheckUserService checkUserService) {
        this.orderService = orderService;
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.checkUserService = checkUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderCreatedResponseDto> createOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody OrderRequestDto orderRequestDto
    ) throws Exception {
        if (!checkUserService.checkUser(session)) {
            OrderCreatedResponseDto response = new OrderCreatedResponseDto(
                    "",
                    "доступ запрещен",
                    0
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        String idOrder = UUID.randomUUID().toString();
        String status = "new";

        Random random = new Random();
        int confirmCode = random.nextInt(9000) + 1000;
        etcdService.putTemporary("code:" + idOrder, Integer.toString(confirmCode), 180);

        String jsonUser = etcdService.get("session:" + session);
        User user = objectMapper.readValue(jsonUser, User.class);

        Order order = new Order(
                idOrder,
                status,
                user.getUsername(),
                orderRequestDto.getIdProduct(),
                orderRequestDto.getNameProduct(),
                orderRequestDto.getAmount(),
                orderRequestDto.getCost()
        );

        orderService.putOrder(order);

        OrderCreatedResponseDto response = new OrderCreatedResponseDto(
                idOrder,
                "заказ создан, номер заказа: " + idOrder,
                confirmCode
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/userOrders")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String jsonUser = etcdService.get("session:" + session);
        User user = objectMapper.readValue(jsonUser, User.class);

        List<Order> orders = orderService.getUserOrders(user.getUsername());

        List<OrderResponseDto> ordersResponse = orders.stream()
                .map(order -> new OrderResponseDto(
                        order.getId(),
                        order.getStatus(),
                        order.getUsername(),
                        order.getNameProduct(),
                        order.getAmount(),
                        order.getCost()
                ))
                .toList();

        return ResponseEntity.ok(ordersResponse);
    }

    @GetMapping("/allOrders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Order> orders = orderService.getAllOrders();

        List<OrderResponseDto> ordersResponse = orders.stream()
                .map(order -> new OrderResponseDto(
                        order.getId(),
                        order.getStatus(),
                        order.getUsername(),
                        order.getNameProduct(),
                        order.getAmount(),
                        order.getCost()
                ))
                .toList();

        return ResponseEntity.ok(ordersResponse);
    }

    @PostMapping("/moderate")
    public ResponseEntity<String> moderateOrder(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody StatusRequestDto statusRequestDto
    ) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String idOrder = statusRequestDto.getIdOrder();

        Order order = orderService.getOrder(idOrder);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("заказ не найден");
        }

        order.setStatus(statusRequestDto.getNewStatus());
        orderService.putOrder(order);

        return ResponseEntity.ok("статус заказа " + idOrder + " успешно изменен");
    }

    @PostMapping("/printCode")
    public ResponseEntity<String> checkCode(
            @CookieValue(name = "SESSION", required = false) String session,
            @RequestBody CodeRequestDto codeRequestDto
            ) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String jsonUser = etcdService.get("session:" + session);
        User user = objectMapper.readValue(jsonUser, User.class);

        String idOrder = codeRequestDto.getIdOrder();

        Order order = orderService.getOrder(idOrder);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("заказ не найден");
        }

        String codeJson = etcdService.get("code:" + idOrder);
        int code = objectMapper.readValue(codeJson, Integer.class);

        String message;

        if (codeRequestDto.getCode() == code) {
            order.setStatus("confirmed");
            message = "создание заказа подтвёрждено, ожидайте решение модератора";
        } else {
            order.setStatus("deleted");
            message = "вы не подтвердили заказ";
        }

        orderService.putOrder(order);

        return ResponseEntity.ok(message);
    }
}