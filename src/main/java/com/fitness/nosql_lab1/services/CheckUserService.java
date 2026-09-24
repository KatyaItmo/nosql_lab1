package com.fitness.nosql_lab1.services;

import com.fitness.nosql_lab1.objects.Role;
import com.fitness.nosql_lab1.objects.User;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class CheckUserService {
    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;

    public CheckUserService(EtcdService etcdService, ObjectMapper objectMapper) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
    }

    public boolean checkModer(String session) throws Exception {
        String jsonUser = etcdService.get("session:" + session);

        if (jsonUser == null) {
            return false;
        }

        User user = objectMapper.readValue(jsonUser, User.class);

        return user.getRole().equals(Role.MODER);
    }

    public boolean checkUser(String session) throws Exception {
        String jsonUser = etcdService.get("session:" + session);

        return jsonUser != null;
    }
}
