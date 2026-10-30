package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.dtos.UserRegistration;
import com.fitness.nosql_lab1.objects.User;
import com.fitness.nosql_lab1.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EtcdService etcdService;
    private final SessionService sessionService;
    private final MessageService messageService;

    public UserService(UserRepository userRepository, EtcdService etcdService, SessionService sessionService, MessageService messageService) {
        this.userRepository = userRepository;
        this.etcdService = etcdService;
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    public ResponseEntity<String> registerUser(UserRegistration userDto) {
        if (userDto.getRole().equals("moder")) {
            if (!userDto.getSecreteCode().equals("cheremsha")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("неверный код модератора");
            }
        }

        if (checkUser(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userDto.getUsername() + " уже зарегистрирован");
        }

        User user = new User(
                userDto.getUsername(),
                userDto.getPassword(),
                userDto.getRole()
        );

        userRepository.save(user);

        if (user.getRole().equals("moder")) {
            messageService.makeNewsletter("moder", "НОВЫЙ МОДЕРАТОР", "в системе зарегистрировался новый модератор " + user.getUsername());
            return ResponseEntity.ok("зарегистрирован модератор " + user.getUsername());
        }
        messageService.makeNewsletter("moder", "НОВЫЙ ПОЛЬЗОВАТЕЛЬ", "в системе зарегистрировался новый пользователь " + user.getUsername());
        return ResponseEntity.ok("зарегистрирован пользователь " + user.getUsername());
    }


    public ResponseEntity<String> loginUser(User userData) throws Exception {
        if (!checkUser(userData.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("нет такого пользователя");
        }

        String countKey = "counter:" + userData.getUsername();

        User userDb = userRepository.findById(userData.getUsername()).orElse(null);

        assert userDb != null;
        if (!userDb.getPassword().equals(userData.getPassword())) {
            if (etcdService.processCounter(userData.getUsername(), 100, 5, false) >= 5) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("вы превысили число попыток входа");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("неверный пароль");
        }

        if (userDb.getRole().equals("user") && userData.getRole().equals("moder")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("вы не модератор");
        }

        if (etcdService.processCounter(userData.getUsername(), 100, 5, true) >= 5) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ваш аккаунт временно недоступен (превышено число попыток входа)");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionService.login(userData).toString())
                .body("выполнен вход под логином " + userData.getUsername());
    }


    public ResponseEntity<String> logoutUser(String sessionToken) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionService.logout(sessionToken).toString())
                .body("вы вышли из аккаунта");
    }


    public List<User> getUsers(String role) {
        return userRepository.findByRole(role);
    }

    private boolean checkUser(String username) {
        return userRepository.findById(username).isPresent();
    }
}
