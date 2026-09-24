package com.fitness.nosql_lab1.objects;

public class Order {
    private String id;
    private Status status;
    private String username;
    private String clientName;
    private String subscriptionType;
    private int period;
    private int amount;

    public Order() {}

    public Order(String id, Status status, String username, String clientName, String subscriptionType, int period, int amount) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.clientName = clientName;
        this.subscriptionType = subscriptionType;
        this.period = period;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
