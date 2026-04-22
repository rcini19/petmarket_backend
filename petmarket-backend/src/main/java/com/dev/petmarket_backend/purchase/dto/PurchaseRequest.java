package com.dev.petmarket_backend.purchase.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PurchaseRequest {

    @NotNull(message = "Pet id is required")
    private Long petId;

    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
