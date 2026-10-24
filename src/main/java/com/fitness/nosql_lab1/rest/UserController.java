package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.UserRequestDto;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.services.EtcdService;
import com.fitness.nosql_lab1.services.UserService;
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
    private final UserService userService;
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, EtcdService etcdService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername();

        if (checkUser(username)) {
            User user = new User(
                    username,
                    userRequestDto.getPassword(),
                    userRequestDto.getRole()
            );

            userService.putUser(user);

            if ("moder".equals(user.getRole())) {
                return ResponseEntity.ok("зарегистрирован модератор " + username);
            }
            return ResponseEntity.ok("зарегистрирован пользователь " + username);
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("пользователь " + username + " уже существует");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDto userRequestDto) throws Exception {
        String username = userRequestDto.getUsername();
        if (!checkUser(username)) {
            User user = userService.getUser(username);

            String keyCount = "counter:" + username;

            if (user.getPassword().equals(userRequestDto.getPassword())) {
                if (user.getRole().equals("user") && userRequestDto.getRole().equals("moder")) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("вы не модератор");
                }

                if (etcdService.processLoginAttempt(keyCount, true) == 3) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("вы превысили число попыток входа");
                }

                String sessionToken = UUID.randomUUID().toString();

                String userJson = objectMapper.writeValueAsString(user);
                etcdService.putTemporary("session:" + sessionToken, userJson, 3600);

                ResponseCookie cookie = ResponseCookie.from("SESSION", sessionToken)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(3600)
                        .sameSite("Lax")
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body("выполнен вход под логином " + username);
            }

            if (etcdService.processLoginAttempt(keyCount, false) == 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("вы превысили число попыток входа");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("неверный логин или пароль");
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("нет такого пользователя");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        if (session != null) {
            etcdService.delete("session:" + session);
        }

        ResponseCookie logoutCookie = ResponseCookie.from("SESSION", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
                .body("вы вышли из аккаунта");
    }

    private boolean checkUser(String username) {
        User user = userService.getUser(username);
        return (user == null);
    }
}