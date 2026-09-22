package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.UserDto;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.services.EtcdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;

    public UserController(EtcdService etcdService, ObjectMapper objectMapper) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) throws Exception{
        String role = userDto.getRole();
        String username = userDto.getUsername();
        String password = userDto.getPassword();

        if (checkUser("user:" + role + ":" + username)) {
            String userID = UUID.randomUUID().toString();

            User user = new User(
                    role,
                    username,
                    password
            );

            etcdService.put("user:" + role + ":" + username, objectMapper.writeValueAsString(user));
            return ResponseEntity.ok("Зарегистрирован пользователь " + username);
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с логином " + username + " уже зарегистрирован");

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) throws Exception {
        String role = userDto.getRole();
        String username = userDto.getUsername();
        String password = userDto.getPassword();

        if (!checkUser("user:" + role + ":" + username)) {
            String jsonUser = etcdService.get("user:" + role + ":" + username);
            User user = objectMapper.readValue(jsonUser, User.class);

            if (user.getPassword().equals(password)) {
                // должен появиться создание куки

                return ResponseEntity.ok("Выполнен вход под логином " + username);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
    }

    private boolean checkUser(String username) throws Exception {
        String user = etcdService.get(username);
        return (user == null);
    }
}
