package com.dev.petmarket_backend.admin.dto;

public class AdminUserResponse {

    private String email;
    private String fullName;
    private String role;
    private String message;

    public AdminUserResponse(String email, String fullName, String role, String message) {
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
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
