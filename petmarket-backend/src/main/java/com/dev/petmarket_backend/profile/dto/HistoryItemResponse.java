package com.dev.petmarket_backend.profile.dto;

public class HistoryItemResponse {

    private String id;
    private String title;
    private String date;
    private String subtitle;
    private String status;
    private Double amount;

    public HistoryItemResponse(String id, String title, String date, String subtitle, String status, Double amount) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.subtitle = subtitle;
        this.status = status;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
