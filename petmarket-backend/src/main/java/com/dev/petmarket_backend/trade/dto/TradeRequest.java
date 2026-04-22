package com.dev.petmarket_backend.trade.dto;

import jakarta.validation.constraints.NotNull;

public class TradeRequest {

    @NotNull(message = "Offered pet id is required")
    private Long offeredPetId;

    @NotNull(message = "Requested pet id is required")
    private Long requestedPetId;

    public Long getOfferedPetId() {
        return offeredPetId;
    }

    public void setOfferedPetId(Long offeredPetId) {
        this.offeredPetId = offeredPetId;
    }

    public Long getRequestedPetId() {
        return requestedPetId;
    }

    public void setRequestedPetId(Long requestedPetId) {
        this.requestedPetId = requestedPetId;
    }
}
