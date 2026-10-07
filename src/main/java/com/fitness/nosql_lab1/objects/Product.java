package com.fitness.nosql_lab1.objects;

public class Product {
    private String productID;
    private String name;
    private String description;
    private int cost;

    public Product(String productID, String name, String description, int cost) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    public Product() {}

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
