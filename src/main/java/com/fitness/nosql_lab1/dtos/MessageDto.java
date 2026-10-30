package com.fitness.nosql_lab1.dtos;

public class MessageDto {
    private String id;
    private String header;
    private String message;
    private boolean isChecked;

    public MessageDto() {}

    public MessageDto(String id, String header, String message, boolean isChecked) {
        this.id = id;
        this.header = header;
        this.message = message;
        this.isChecked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
