package com.fitness.nosql_lab1.objects;

public class Order {
    private String username;
    private String orderID;
    private String status;
    private String idProduct;
    private String nameProduct;
    private int amount;
    private long cost;

    public Order() {}

    public Order(String username, String orderID, String status, String idProduct, String nameProduct, int amount, long cost) {
        this.username = username;
        this.orderID = orderID;
        this.status = status;
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.amount = amount;
        this.cost = cost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
