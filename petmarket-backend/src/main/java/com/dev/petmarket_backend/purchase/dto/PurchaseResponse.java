package com.dev.petmarket_backend.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseResponse {

    private Long id;
    private Long buyerId;
    private String buyerName;
    private Long petId;
    private String petName;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public PurchaseResponse(Long id,
                            Long buyerId,
                            String buyerName,
                            Long petId,
                            String petName,
                            BigDecimal totalPrice,
                            LocalDateTime createdAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.petId = petId;
        this.petName = petName;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public Long getPetId() {
        return petId;
    }

    public String getPetName() {
        return petName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
