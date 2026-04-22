package com.dev.petmarket_backend.purchase.service;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.pet.model.PetListing;
import com.dev.petmarket_backend.pet.repository.PetListingRepository;
import com.dev.petmarket_backend.purchase.dto.PurchaseRequest;
import com.dev.petmarket_backend.purchase.dto.PurchaseResponse;
import com.dev.petmarket_backend.purchase.model.Purchase;
import com.dev.petmarket_backend.purchase.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PetListingRepository petListingRepository;
    private final UserRepository userRepository;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           PetListingRepository petListingRepository,
                           UserRepository userRepository) {
        this.purchaseRepository = purchaseRepository;
        this.petListingRepository = petListingRepository;
        this.userRepository = userRepository;
    }

    public PurchaseResponse createPurchase(String requesterEmail, PurchaseRequest request) {
        User buyer = userRepository.findByEmailIgnoreCase(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Buyer account not found"));

        PetListing pet = petListingRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet listing not found"));

        if (pet.getOwner().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You cannot purchase your own pet");
        }

        if (purchaseRepository.existsByPet_Id(pet.getId())) {
            throw new IllegalArgumentException("A pet may only be purchased once");
        }

        if (!"AVAILABLE".equalsIgnoreCase(pet.getStatus())) {
            throw new IllegalArgumentException("This pet is no longer available");
        }

        String listingType = pet.getListingType() == null ? "" : pet.getListingType().toUpperCase();
        if (!"SALE".equals(listingType) && !"BOTH".equals(listingType)) {
            throw new IllegalArgumentException("Only pets marked as Sale can be purchased");
        }

        if (pet.getPrice() == null || pet.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("This listing has no valid sale price");
        }

        if (request.getTotalPrice().compareTo(pet.getPrice()) != 0) {
            throw new IllegalArgumentException("Submitted total price does not match listing price");
        }

        Purchase purchase = new Purchase();
        purchase.setBuyer(buyer);
        purchase.setPet(pet);
        purchase.setTotalPrice(request.getTotalPrice());
        purchase.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        purchase.setStatus("COMPLETED");

        // Transfer ownership to buyer and mark as sold
        pet.setOwner(buyer);
        pet.setStatus("SOLD");
        petListingRepository.save(pet);

        Purchase saved = purchaseRepository.save(purchase);
        return toResponse(saved);
    }

    public PurchaseResponse getPurchase(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        return toResponse(purchase);
    }

    private PurchaseResponse toResponse(Purchase purchase) {
        return new PurchaseResponse(
                purchase.getId(),
                purchase.getBuyer().getId(),
                purchase.getBuyer().getFullName(),
                purchase.getPet().getId(),
                purchase.getPet().getName(),
                purchase.getTotalPrice(),
                purchase.getCreatedAt()
        );
    }
}
