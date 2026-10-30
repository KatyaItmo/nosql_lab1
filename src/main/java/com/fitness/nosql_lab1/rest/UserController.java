package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.UserRegistration;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistration user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) throws Exception {
        return userService.loginUser(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = "SESSION", required = false) String session
    ) throws Exception {
        return userService.logoutUser(session);
    }
}