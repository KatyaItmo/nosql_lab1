package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.User;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class SessionService {
    private final ObjectMapper objectMapper;
    private final EtcdService etcdService;

    public SessionService(ObjectMapper objectMapper, EtcdService etcdService) {
        this.objectMapper = objectMapper;
        this.etcdService = etcdService;
    }

    public ResponseCookie login(User user) throws Exception {
        String sessionToken = UUID.randomUUID().toString();

        String userJson = objectMapper.writeValueAsString(user);
        etcdService.putTemporary("session:" + sessionToken, userJson, 3600);

        return ResponseCookie.from("SESSION", sessionToken)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie logout(String sessionToken) throws Exception {
        if (sessionToken != null) {
            etcdService.delete("session:" + sessionToken);
            etcdService.delete("cart:" + sessionToken);
        }

        return ResponseCookie.from("SESSION", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public boolean checkUser(String sessionToken, String role) throws Exception {
        if (sessionToken == null || sessionToken.isBlank()) {
            return false;
        }

        String userJson = etcdService.get("session:" + sessionToken);
        if (userJson == null) {
            return false;
        }

        if (role.equals("user")) {
            return true;
        }

        User user = objectMapper.readValue(userJson, User.class);
        return (user.getRole().equals(role));
    }

    public User getUser(String sessionToken) throws Exception {
        String userJson = etcdService.get("session:" + sessionToken);
        return objectMapper.readValue(userJson, User.class);
    }
}
