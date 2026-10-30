package com.fitness.nosql_lab1.dtos;

import java.util.List;

public class OrderDto {
    private String id;
    private String status;
    private String username;
    private List<OrderItemDto> orderList;
    private long cost;

    public OrderDto() {}

    public OrderDto(String id, String status, String username, List<OrderItemDto> orderList, long cost) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.orderList = orderList;
        this.cost = cost;
    }

    public OrderDto(List<OrderItemDto> orderList, long cost) {
        this.orderList = orderList;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OrderItemDto> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderItemDto> orderList) {
        this.orderList = orderList;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
