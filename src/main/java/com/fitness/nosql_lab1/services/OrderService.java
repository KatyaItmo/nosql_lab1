package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void putOrder(Order order) {
        orderRepository.save(order);
    }

    public Order getOrder(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getUserOrders(String username) {
        return orderRepository.findByUsername(username);
    }
}
