package com.fitness.nosql_lab1.dtos;

public class DecisionDto {
    private String orderID;
    private String decision;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
