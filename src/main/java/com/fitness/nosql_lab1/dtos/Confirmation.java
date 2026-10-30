package com.fitness.nosql_lab1.dtos;

public class Confirmation {
    private String orderId;
    private int code;

    public Confirmation() {}

    public Confirmation(String orderId, int code) {
        this.orderId = orderId;
        this.code = code;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
