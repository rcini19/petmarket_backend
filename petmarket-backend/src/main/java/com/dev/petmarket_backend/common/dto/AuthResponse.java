package com.dev.petmarket_backend.common.dto;

public class AuthResponse {

    private String token;
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private String message;

    public AuthResponse(String token, Long id, String email, String fullName, String role, String message) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public AuthResponse(String token, String email, String fullName, String role, String message) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
