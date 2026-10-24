package com.fitness.nosql_lab1.objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String idProduct;

    @Column(nullable = false)
    private String nameProduct;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private long cost;

    public Order() {}

    public Order(String id, String status, String username, String idProduct, String nameProduct, int amount, long cost) {
        this.id = id;
        this.status = status;
        this.username = username;
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

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
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
