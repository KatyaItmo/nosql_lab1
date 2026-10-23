package com.fitness.nosql_lab1.objects;

public class Product {
    private String productID;
    private String name;
    private String description;
    private int price;

    public Product(String productID, String name, String description, int price) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
