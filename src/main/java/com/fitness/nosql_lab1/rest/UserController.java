package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.UserDto;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.services.EtcdService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
                String sessionToken = UUID.randomUUID().toString();
                etcdService.put("session:" + sessionToken, objectMapper.writeValueAsString(user));

                ResponseCookie cookie = ResponseCookie.from("SESSION", sessionToken)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(3600)
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body("Выполнен вход под логином " + username);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "SESSION") String session) throws Exception {
        etcdService.delete("session:" + session);

        ResponseCookie logoutCookie = ResponseCookie.from("SESSION", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
                .body("Выход выполнен");
    }

    private boolean checkUser(String username) throws Exception {
        String user = etcdService.get(username);
        return (user == null);
    }
}
