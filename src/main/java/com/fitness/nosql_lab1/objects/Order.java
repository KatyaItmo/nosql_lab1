package com.fitness.nosql_lab1.objects;

public class Order {
    private String orderID;
    private String status;
    private String idProduct;
    private int amount;

    public Order() {}

    public Order(String orderID, String status, String idProduct, int amount) {
        this.orderID = orderID;
        this.status = status;
        this.idProduct = idProduct;
        this.amount = amount;
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
}
