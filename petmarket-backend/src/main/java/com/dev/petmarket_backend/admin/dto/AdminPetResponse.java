package com.dev.petmarket_backend.admin.dto;

import java.math.BigDecimal;

public class AdminPetResponse {

    private Long id;
    private String name;
    private String species;
    private String breed;
    private String listingType;
    private String status;
    private BigDecimal price;
    private String ownerName;

    public AdminPetResponse(Long id,
                            String name,
                            String species,
                            String breed,
                            String listingType,
                            String status,
                            BigDecimal price,
                            String ownerName) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.listingType = listingType;
        this.status = status;
        this.price = price;
        this.ownerName = ownerName;
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

    public String getListingType() {
        return listingType;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
