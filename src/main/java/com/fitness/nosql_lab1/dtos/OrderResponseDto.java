package com.fitness.nosql_lab1.dtos;

public class OrderResponseDto {
    private String id;
    private String status;
    private String username;
    private String nameProduct;
    private int amount;
    private long cost;

    public OrderResponseDto() {}

    public OrderResponseDto(String id, String status, String username, String nameProduct, int amount, long cost) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.nameProduct = nameProduct;
        this.amount = amount;
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

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
