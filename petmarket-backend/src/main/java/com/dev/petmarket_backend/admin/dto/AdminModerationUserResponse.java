package com.dev.petmarket_backend.admin.dto;

import java.time.LocalDateTime;

public class AdminModerationUserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private boolean suspended;
    private LocalDateTime joinedAt;
    private long purchases;
    private long trades;

    public AdminModerationUserResponse(Long id,
                                       String fullName,
                                       String email,
                                       String role,
                                       boolean suspended,
                                       LocalDateTime joinedAt,
                                       long purchases,
                                       long trades) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.suspended = suspended;
        this.joinedAt = joinedAt;
        this.purchases = purchases;
        this.trades = trades;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public long getPurchases() {
        return purchases;
    }

    public long getTrades() {
        return trades;
    }
}
