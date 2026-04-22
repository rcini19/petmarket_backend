package com.dev.petmarket_backend.common.model;

import com.dev.petmarket_backend.auth.model.RefreshToken;
import com.dev.petmarket_backend.pet.model.PetListing;
import com.dev.petmarket_backend.purchase.model.Purchase;
import com.dev.petmarket_backend.trade.model.TradeOffer;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, length = 50)
    private String role = "USER";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "profile_image_data", columnDefinition = "BYTEA")
    private byte[] profileImageData;

    @Column(name = "profile_image_content_type", length = 50)
    private String profileImageContentType;

    @Column(nullable = false)
    private boolean suspended = false;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<PetListing> pets = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
    private List<Purchase> orders = new ArrayList<>();

    @OneToMany(mappedBy = "offeringUser", fetch = FetchType.LAZY)
    private List<TradeOffer> tradeOffers = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    public User() {
    }

    public User(String email, String passwordHash, String fullName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = "USER";
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getProfileImageData() {
        return profileImageData;
    }

    public void setProfileImageData(byte[] profileImageData) {
        this.profileImageData = profileImageData;
    }

    public String getProfileImageContentType() {
        return profileImageContentType;
    }

    public void setProfileImageContentType(String profileImageContentType) {
        this.profileImageContentType = profileImageContentType;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public List<PetListing> getPets() {
        return pets;
    }

    public void setPets(List<PetListing> pets) {
        this.pets = pets;
    }

    public List<Purchase> getOrders() {
        return orders;
    }

    public void setOrders(List<Purchase> orders) {
        this.orders = orders;
    }

    public List<TradeOffer> getTradeOffers() {
        return tradeOffers;
    }

    public void setTradeOffers(List<TradeOffer> tradeOffers) {
        this.tradeOffers = tradeOffers;
    }

    public List<RefreshToken> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }
}
