package com.fitness.nosql_lab1.dtos;

public class Code {
    private String message;
    private String orderId;
    private int code;

    public Code() {}

    public Code(String message, String orderId, int code) {
        this.message = message;
        this.orderId = orderId;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
