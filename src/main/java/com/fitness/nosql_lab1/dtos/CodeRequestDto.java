package com.fitness.nosql_lab1.dtos;

public class CodeRequestDto {
    private String idOrder;
    private int code;

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
