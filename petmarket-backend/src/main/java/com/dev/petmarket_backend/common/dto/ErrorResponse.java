package com.dev.petmarket_backend.common.dto;

public class ErrorResponse {

    private String error;
    private String message;

    public ErrorResponse(String error) {
        this.error = error;
        this.message = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        this.message = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
