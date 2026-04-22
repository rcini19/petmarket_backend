package com.dev.petmarket_backend.trade.model;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.pet.model.PetListing;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade_offers")
public class TradeOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offered_pet_id", nullable = false)
    private PetListing offeredPet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_pet_id", nullable = false)
    private PetListing requestedPet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offered_by_user_id", nullable = false)
    private User offeringUser;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PetListing getOfferedPet() {
        return offeredPet;
    }

    public void setOfferedPet(PetListing offeredPet) {
        this.offeredPet = offeredPet;
    }

    public PetListing getRequestedPet() {
        return requestedPet;
    }

    public void setRequestedPet(PetListing requestedPet) {
        this.requestedPet = requestedPet;
    }

    public User getOfferingUser() {
        return offeringUser;
    }

    public void setOfferingUser(User offeringUser) {
        this.offeringUser = offeringUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
