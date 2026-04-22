package com.dev.petmarket_backend.pet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PetResponse {

    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String listingType;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private String status;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;

    public PetResponse(Long id,
                       String name,
                       String species,
                       String breed,
                       Integer age,
                       String listingType,
                       BigDecimal price,
                       String description,
                       String imageUrl,
                       String status,
                       Long ownerId,
                       String ownerName,
                       LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.listingType = listingType;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public Integer getAge() {
        return age;
    }

    public String getListingType() {
        return listingType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
