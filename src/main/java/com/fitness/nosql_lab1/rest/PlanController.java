package com.fitness.nosql_lab1.rest;

import com.fitness.nosql_lab1.dtos.PlanDto;
import com.fitness.nosql_lab1.objects.Plan;
import com.fitness.nosql_lab1.services.CheckUserService;
import com.fitness.nosql_lab1.services.EtcdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final EtcdService etcdService;
    private final ObjectMapper objectMapper;
    private final CheckUserService checkUserService;

    public PlanController(EtcdService etcdService, ObjectMapper objectMapper, CheckUserService checkUserService) {
        this.etcdService = etcdService;
        this.objectMapper = objectMapper;
        this.checkUserService = checkUserService;
    }

    @PostMapping()
    public ResponseEntity<String> createPlan(@CookieValue(name = "SESSION") String session, @RequestBody PlanDto planDto) throws Exception {
        if (!checkUserService.checkModer(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещен");
        }

        String planID = UUID.randomUUID().toString();

        Plan plan = new Plan(
                planID,
                planDto.getName(),
                planDto.getDescription(),
                planDto.getMonthCost(),
                planDto.getYearCost()
        );

        etcdService.put("plan:" + planID, objectMapper.writeValueAsString(plan));

        return ResponseEntity.ok("План добавлен в базу, ID плана: " + planID);
    }

    @GetMapping
    public ResponseEntity<List<Plan>> getPlans(@CookieValue(name = "SESSION") String session) throws Exception {
        if (!checkUserService.checkUser(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> jsonList = etcdService.getList("plan:");

        List<Plan> plansList = jsonList.stream()
                .map(json -> {
                    return objectMapper.readValue(json, Plan.class);
                })
                .toList();

        return ResponseEntity.ok(plansList);

    }
}
