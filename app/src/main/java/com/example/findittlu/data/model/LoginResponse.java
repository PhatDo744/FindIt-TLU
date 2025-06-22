package com.example.findittlu.data.model;

public class LoginResponse {
    private String token;
    private User user;
    private String message;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 