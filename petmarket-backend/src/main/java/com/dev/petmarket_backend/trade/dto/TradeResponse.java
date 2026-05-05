package com.dev.petmarket_backend.trade.dto;

import java.time.LocalDateTime;

public class TradeResponse {

    private Long id;
    private Long offeredPetId;
    private String offeredPetName;
    private Long offeredPetOwnerId;
    private String offeredPetOwnerName;
    private Long requestedPetId;
    private String requestedPetName;
    private Long requestedPetOwnerId;
    private String requestedPetOwnerName;
    private Long offeringUserId;
    private String offeringUserName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public TradeResponse(Long id,
                         Long offeredPetId,
                         String offeredPetName,
                         Long offeredPetOwnerId,
                         String offeredPetOwnerName,
                         Long requestedPetId,
                         String requestedPetName,
                         Long requestedPetOwnerId,
                         String requestedPetOwnerName,
                         Long offeringUserId,
                         String offeringUserName,
                         String status,
                         LocalDateTime createdAt,
                         LocalDateTime respondedAt) {
        this.id = id;
        this.offeredPetId = offeredPetId;
        this.offeredPetName = offeredPetName;
        this.offeredPetOwnerId = offeredPetOwnerId;
        this.offeredPetOwnerName = offeredPetOwnerName;
        this.requestedPetId = requestedPetId;
        this.requestedPetName = requestedPetName;
        this.requestedPetOwnerId = requestedPetOwnerId;
        this.requestedPetOwnerName = requestedPetOwnerName;
        this.offeringUserId = offeringUserId;
        this.offeringUserName = offeringUserName;
        this.status = status;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
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

    public Long getOfferedPetOwnerId() {
        return offeredPetOwnerId;
    }

    public String getOfferedPetOwnerName() {
        return offeredPetOwnerName;
    }

    public Long getRequestedPetId() {
        return requestedPetId;
    }

    public String getRequestedPetName() {
        return requestedPetName;
    }

    public Long getRequestedPetOwnerId() {
        return requestedPetOwnerId;
    }

    public String getRequestedPetOwnerName() {
        return requestedPetOwnerName;
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

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
}
