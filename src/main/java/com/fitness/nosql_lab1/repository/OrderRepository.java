package com.fitness.nosql_lab1.repository;

import com.fitness.nosql_lab1.objects.Order;
import com.fitness.nosql_lab1.objects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUser(User user);
}