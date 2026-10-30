package com.fitness.nosql_lab1.dtos;

public class OrderItemDto {
    private String productId;
    private String productName;
    private int productPrice;
    private int amount;

    public OrderItemDto() {}

    public OrderItemDto(String productId, String productName, int productPrice, int amount) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
