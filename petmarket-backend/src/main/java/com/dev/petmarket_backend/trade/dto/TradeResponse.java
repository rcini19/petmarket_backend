package com.dev.petmarket_backend.trade.dto;

import java.time.LocalDateTime;

public class TradeResponse {

    private Long id;
    private Long offeredPetId;
    private String offeredPetName;
    private Long requestedPetId;
    private String requestedPetName;
    private Long offeringUserId;
    private String offeringUserName;
    private String status;
    private LocalDateTime createdAt;

    public TradeResponse(Long id,
                         Long offeredPetId,
                         String offeredPetName,
                         Long requestedPetId,
                         String requestedPetName,
                         Long offeringUserId,
                         String offeringUserName,
                         String status,
                         LocalDateTime createdAt) {
        this.id = id;
        this.offeredPetId = offeredPetId;
        this.offeredPetName = offeredPetName;
        this.requestedPetId = requestedPetId;
        this.requestedPetName = requestedPetName;
        this.offeringUserId = offeringUserId;
        this.offeringUserName = offeringUserName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getOfferedPetId() {
        return offeredPetId;
    }

    public String getOfferedPetName() {
        return offeredPetName;
    }

    public Long getRequestedPetId() {
        return requestedPetId;
    }

    public String getRequestedPetName() {
        return requestedPetName;
    }

    public Long getOfferingUserId() {
        return offeringUserId;
    }

    public String getOfferingUserName() {
        return offeringUserName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
