package com.fitness.nosql_lab1.dtos;

public class Moderation {
    private String orderId;
    private String clientName;
    private String newStatus;

    public Moderation() {}

    public Moderation(String orderId, String clientName, String newStatus) {
        this.orderId = orderId;
        this.clientName = clientName;
        this.newStatus = newStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
