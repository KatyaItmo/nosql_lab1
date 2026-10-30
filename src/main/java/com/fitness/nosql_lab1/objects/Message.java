package com.fitness.nosql_lab1.objects;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String header;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked;

    public Message() {}

    public Message(String id, User user, String header, String message, boolean isChecked) {
        this.id = id;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
