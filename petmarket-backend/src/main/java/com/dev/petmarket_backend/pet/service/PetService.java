package com.dev.petmarket_backend.pet.service;

import com.dev.petmarket_backend.common.dto.PageInfo;
import com.dev.petmarket_backend.common.dto.PaginatedResponse;
import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.pet.dto.PetRequest;
import com.dev.petmarket_backend.pet.dto.PetResponse;
import com.dev.petmarket_backend.pet.model.PetListing;
import com.dev.petmarket_backend.pet.repository.PetListingRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PetService {

    private final PetListingRepository petListingRepository;
    private final UserRepository userRepository;

    public PetService(PetListingRepository petListingRepository, UserRepository userRepository) {
        this.petListingRepository = petListingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get pets with pagination support
     */
    public PaginatedResponse<PetResponse> getPetsWithPagination(String search, String listingType, String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<PetListing> spec = buildPetSpecification(search, listingType, status);
        Page<PetListing> pageResult = petListingRepository.findAll(spec, pageable);

        List<PetResponse> content = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        PageInfo pageInfo = new PageInfo(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return new PaginatedResponse<>(content, pageInfo);
    }

    /**
     * Get user's pets with pagination support
     */
    public PaginatedResponse<PetResponse> getMyPetsWithPagination(String requesterEmail, int page, int pageSize) {
        User user = getUserByEmail(requesterEmail);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PetListing> pageResult = petListingRepository.findByOwnerAndStatus(user, "AVAILABLE", pageable);

        List<PetResponse> content = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        PageInfo pageInfo = new PageInfo(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return new PaginatedResponse<>(content, pageInfo);
    }

    // Legacy methods (kept for backward compatibility)
    public List<PetResponse> getPets(String search, String listingType, String status) {
        Specification<PetListing> spec = buildPetSpecification(search, listingType, status);
        return petListingRepository.findAll(spec)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PetResponse> getMyPets(String requesterEmail) {
        User user = getUserByEmail(requesterEmail);
        return petListingRepository.findByOwnerAndStatusOrderByCreatedAtDesc(user, "AVAILABLE")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PetResponse getPetById(Long petId) {
        PetListing pet = petListingRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet listing not found"));

        return toResponse(pet);
    }

    public PetResponse createPet(String requesterEmail, PetRequest request) {
        User owner = getUserByEmail(requesterEmail);

        PetListing pet = new PetListing();
        applyRequestToPet(pet, request);
        pet.setOwner(owner);
        pet.setStatus("AVAILABLE");

        return toResponse(petListingRepository.save(pet));
    }

    public PetResponse updatePet(String requesterEmail, Long petId, PetRequest request) {
        User requester = getUserByEmail(requesterEmail);
        PetListing pet = petListingRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet listing not found"));

        if (!pet.getOwner().getId().equals(requester.getId())) {
            throw new SecurityException("Only the listing owner may edit this listing");
        }

        if (!"AVAILABLE".equalsIgnoreCase(pet.getStatus())) {
            throw new IllegalArgumentException("Only available listings can be edited");
        }

        applyRequestToPet(pet, request);
        return toResponse(petListingRepository.save(pet));
    }

    public void deletePet(String requesterEmail, Long petId) {
        User requester = getUserByEmail(requesterEmail);
        PetListing pet = petListingRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet listing not found"));

        if (!pet.getOwner().getId().equals(requester.getId())) {
            throw new SecurityException("Only the listing owner may delete this listing");
        }

        if (!"AVAILABLE".equalsIgnoreCase(pet.getStatus())) {
            throw new IllegalArgumentException("Only available listings can be deleted");
        }

        petListingRepository.delete(pet);
    }

    public void deletePetAsAdmin(Long petId) {
        PetListing pet = petListingRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet listing not found"));
        petListingRepository.delete(pet);
    }

    private Specification<PetListing> buildPetSpecification(String search, String listingType, String status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String keyword = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keyword),
                        cb.like(cb.lower(root.get("species")), keyword),
                        cb.like(cb.lower(root.get("breed")), keyword)
                ));
            }

            if (listingType != null && !listingType.isBlank()) {
                String normalizedType = normalizeListingType(listingType);
                if ("SALE".equals(normalizedType)) {
                    predicates.add(cb.or(
                            cb.equal(root.get("listingType"), "SALE"),
                            cb.equal(root.get("listingType"), "BOTH")
                    ));
                } else if ("TRADE".equals(normalizedType)) {
                    predicates.add(cb.or(
                            cb.equal(root.get("listingType"), "TRADE"),
                            cb.equal(root.get("listingType"), "BOTH")
                    ));
                }
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.trim().toUpperCase(Locale.ROOT)));
            } else {
                predicates.add(cb.equal(root.get("status"), "AVAILABLE"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void applyRequestToPet(PetListing pet, PetRequest request) {
        String listingType = normalizeListingType(request.getListingType());

        if (!"SALE".equals(listingType) && !"TRADE".equals(listingType) && !"BOTH".equals(listingType)) {
            throw new IllegalArgumentException("Listing type must be Sale or Trade");
        }

        if (("SALE".equals(listingType) || "BOTH".equals(listingType))
                && (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Price is required when listing type is Sale");
        }

        pet.setName(trimRequired(request.getName(), "Pet name is required"));
        pet.setSpecies(trimRequired(request.getSpecies(), "Species is required"));
        pet.setBreed(trimRequired(request.getBreed(), "Breed is required"));
        pet.setAge(request.getAge() == null ? 0 : request.getAge());
        pet.setListingType(listingType);
        pet.setPrice(request.getPrice());
        pet.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        pet.setImageUrl(request.getImageUrl() == null ? "" : request.getImageUrl().trim());
    }

    private String trimRequired(String value, String errorMessage) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized;
    }

    private String normalizeListingType(String listingType) {
        if (listingType == null) {
            return "";
        }

        String normalized = listingType.trim().toUpperCase(Locale.ROOT);
        if ("SALE & TRADE".equals(normalized) || "BOTH".equals(normalized)) {
            return "BOTH";
        }
        return normalized;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private PetResponse toResponse(PetListing pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getAge(),
                pet.getListingType(),
                pet.getPrice(),
                pet.getDescription(),
                pet.getImageUrl(),
                pet.getStatus(),
                pet.getOwner().getId(),
                pet.getOwner().getFullName(),
                pet.getCreatedAt()
        );
    }
}
