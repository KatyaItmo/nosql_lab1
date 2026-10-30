package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.dtos.*;
import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.objects.OrderItem;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final SessionService sessionService;
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    public OrderService(OrderRepository orderRepository, SessionService sessionService, EtcdService etcdService, ObjectMapper objectMapper, MessageService messageService) {
        this.orderRepository = orderRepository;
        this.sessionService = sessionService;
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.messageService = messageService;
    }

    public ResponseEntity<Code> createOrder(String sessionToken, OrderDto orderDto) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            Code code = new Code(
                    "вы не могаете сделать заказ",
                    0
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(code);
        }

        String orderId = UUID.randomUUID().toString();
        String status = "new";
        User user = sessionService.getUser(sessionToken);

        Order order = new Order(
                orderId,
                status,
                user,
                orderDto.getCost()
        );

        List<OrderItem> items = orderDto.getOrderList().stream()
                .map(itemDto -> new OrderItem(
                        UUID.randomUUID().toString(),
                        order,
                        itemDto.getProductId(),
                        itemDto.getProductName(),
                        itemDto.getProductPrice(),
                        itemDto.getAmount()
                ))
                .toList();

        order.setOrderList(items);

        orderRepository.save(order);

        messageService.makeNewsletter(user, "СОЗДАН ЗАКАЗ", "вы создали новый заказ " + orderId + ", подтвердите его в течение 3 минут");

        Random random = new Random();
        int confirmCode = random.nextInt(9000) + 1000;
        etcdService.putTemporary("code:" + orderId, Integer.toString(confirmCode), 240);

        Code code = new Code(
                "создан заказ " + orderId,
                confirmCode
        );
        return ResponseEntity.ok(code);
    }


    public ResponseEntity<List<OrderDto>> getUserOrders(String sessionToken) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        User user = sessionService.getUser(sessionToken);

        List<OrderDto> orders = fromOrdersToOrdersDto(orderRepository.findByUser(user));
        return ResponseEntity.ok(orders);
    }

    public ResponseEntity<List<OrderDto>> getAllOrders(String sessionToken) throws Exception {
        if (!sessionService.checkUser(sessionToken, "moder")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<OrderDto> orders = fromOrdersToOrdersDto(orderRepository.findAll());
        return ResponseEntity.ok(orders);
    }


    public ResponseEntity<String> confirmOrder(String sessionToken, Confirmation confirmation) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        User user = sessionService.getUser(sessionToken);

        if (confirmation.getCode() == 676767) {
            orderRepository.deleteById(confirmation.getOrderId());
            messageService.makeNewsletter(user, "ОТМЕНА ЗАКАЗА", "вы удалили заказ " + confirmation.getOrderId());
            return ResponseEntity.ok("вы отменили заказ " + confirmation.getOrderId());
        }

        if (confirmation.getCode() == 198467) {
            changeStatus(confirmation.getOrderId(), "not_confirmed");
            messageService.makeNewsletter(user, "ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА", "заказ " + confirmation.getOrderId() + " перешел в статус \"не подтвержден\"");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("заказ " + confirmation.getOrderId() + " перешел в статус \"не подтвержден\"");
        }

        String codeJson = etcdService.get("code:" + confirmation.getOrderId());
        if (codeJson == null) {
            changeStatus(confirmation.getOrderId(), "not_confirmed");
            messageService.makeNewsletter(user, "ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА", "заказ " + confirmation.getOrderId() + " перешел в статус \"не подтвержден\"");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("время действия кода истекло, заказ " + confirmation.getOrderId() + " перешел в статус \"не подтвержден\"");
        }

        int code = objectMapper.readValue(codeJson, Integer.class);

        if (confirmation.getCode() == code) {
            changeStatus(confirmation.getOrderId(), "confirmed");
            messageService.makeNewsletter(user, "ПОДТВЕРЖДЕНИЕ ЗАКАЗА", "заказ " + confirmation.getOrderId() + " подтвержден, ожидайте решение модератора");
            messageService.makeNewsletter("moder", "НОВЫЙ ЗАКАЗ", "клиент " + user.getUsername() + " сделал заказ " + confirmation.getOrderId());
            return ResponseEntity.ok("вы подтвердили заказ " + confirmation.getOrderId());
        }
        changeStatus(confirmation.getOrderId(), "not_confirmed");
        messageService.makeNewsletter(user, "ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА", "заказ " + confirmation.getOrderId() + " перешел в статус \"не подтвержден\"");
        return ResponseEntity.status(HttpStatus.CONFLICT).body("неверный код подтверждения, заказ " + confirmation.getOrderId() + " перешел в статус \"не подтвержден\"");
    }

    public ResponseEntity<String> moderateOrder(String sessionToken, Moderation moderation) throws Exception {
        if (!sessionService.checkUser(sessionToken, "moder")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("вы не могаете");
        }

        changeStatus(moderation.getOrderId(), moderation.getNewStatus());

        String textStatus = switch (moderation.getNewStatus()) {
            case "accepted" -> "принят";
            case "declined" -> "отклонен";
            default -> "ошибка в заказе";
        };

        User moder = sessionService.getUser(sessionToken);

        messageService.makeNewsletter(moderation.getClientName(), "ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА", "заказ " + moderation.getOrderId() + " перешел в статус \"" + textStatus + "\"");
        messageService.makeNewsletter("moder", "ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА", "модератор " + moder.getUsername() + " изменил статус заказа " + moderation.getOrderId());
        return ResponseEntity.ok("вы изменили статус заказа " + moderation.getOrderId());
    }


    public ResponseEntity<Code> repeatOrder(String sessionToken, String oldOrderId) throws Exception {
        if (!sessionService.checkUser(sessionToken, "onlyUser")) {
            Code code = new Code(
                    "вы не могаете сделать заказ",
                    0
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(code);
        }

        Order oldOrder = orderRepository.findById(oldOrderId).orElse(null);
        if (oldOrder == null) {
            Code code = new Code(
                    "к сожалению, нельзя повторить данный заказ",
                    0
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(code);
        }

        if (oldOrder.getUser().equals(sessionService.getUser(sessionToken))) {
            Code code = new Code(
                    "действие запрещено",
                    0
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(code);
        }

        String newOrderId = UUID.randomUUID().toString();
        String newOrderStatus = "new";

        Order newOrder = new Order(
                newOrderId,
                newOrderStatus,
                oldOrder.getUser(),
                oldOrder.getOrderList(),
                oldOrder.getCost()
        );

        orderRepository.save(newOrder);

        messageService.makeNewsletter(oldOrder.getUser(), "СОЗДАН ЗАКАЗ", "вы создали новый заказ " + newOrderId + ", подтвердите его в течение 3 минут");

        Random random = new Random();
        int confirmCode = random.nextInt(9000) + 1000;
        etcdService.putTemporary("code:" + newOrderId, Integer.toString(confirmCode), 240);

        Code code = new Code(
                "создан заказ " + newOrderId,
                confirmCode
        );
        return ResponseEntity.ok(code);
    }


    private void changeStatus(String orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(newStatus);
            orderRepository.save(order);
        }
    }

    private List<OrderDto> fromOrdersToOrdersDto(List<Order> orders) {
        return orders.stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getStatus(),
                        order.getUser().getUsername(),
                        order.getOrderList().stream()
                                .map(orderItem -> new OrderItemDto(
                                        orderItem.getProductId(),
                                        orderItem.getProductName(),
                                        orderItem.getProductPrice(),
                                        orderItem.getAmount()
                                ))
                                .toList(),
                        order.getCost()
                ))
                .toList();
    }
}
