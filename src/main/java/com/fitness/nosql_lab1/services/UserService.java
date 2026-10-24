package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void putUser(User user) {
        userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findById(username).orElse(null);
    }
}
