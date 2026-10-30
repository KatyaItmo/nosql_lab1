package com.fitness.nosql_lab1.repository;

import com.fitness.nosql_lab1.objects.Message;
import com.fitness.nosql_lab1.objects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    public List<Message> findByUser(User user);
}
