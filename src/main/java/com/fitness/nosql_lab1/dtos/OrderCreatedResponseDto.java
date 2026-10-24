package com.fitness.nosql_lab1.dtos;

public class OrderCreatedResponseDto {
    private String idOrder;
    private String message;
    private int code;

    public OrderCreatedResponseDto() {}

    public OrderCreatedResponseDto(String idOrder, String message, int code) {
        this.idOrder = idOrder;
        this.message = message;
        this.code = code;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
