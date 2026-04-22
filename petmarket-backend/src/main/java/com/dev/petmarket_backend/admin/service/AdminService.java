package com.dev.petmarket_backend.admin.service;

import com.dev.petmarket_backend.admin.dto.AdminUserResponse;
import com.dev.petmarket_backend.admin.dto.AdminModerationUserResponse;
import com.dev.petmarket_backend.admin.dto.AdminPetResponse;
import com.dev.petmarket_backend.admin.dto.CreateAdminRequest;
import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.pet.repository.PetListingRepository;
import com.dev.petmarket_backend.purchase.repository.PurchaseRepository;
import com.dev.petmarket_backend.trade.repository.TradeOfferRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetListingRepository petListingRepository;
    private final PurchaseRepository purchaseRepository;
    private final TradeOfferRepository tradeOfferRepository;

    public AdminService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        PetListingRepository petListingRepository,
                        PurchaseRepository purchaseRepository,
                        TradeOfferRepository tradeOfferRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.petListingRepository = petListingRepository;
        this.purchaseRepository = purchaseRepository;
        this.tradeOfferRepository = tradeOfferRepository;
    }

    public AdminUserResponse createAdmin(String requesterEmail, CreateAdminRequest request) {
        ensureAdmin(requesterEmail);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String normalizedEmail = normalizeEmail(request.getEmail());
        String fullName = request.getFullName() == null ? "" : request.getFullName().trim();

        if (fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User admin = new User();
        admin.setEmail(normalizedEmail);
        admin.setFullName(fullName);
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setRole("ADMIN");
        userRepository.save(admin);

        return new AdminUserResponse(
                admin.getEmail(),
                admin.getFullName(),
                admin.getRole(),
                "Admin account created successfully"
        );
    }

    public List<AdminPetResponse> getAllPets(String requesterEmail) {
        ensureAdmin(requesterEmail);
        requireModerationDependencies();

        return petListingRepository.findAll()
                .stream()
                .map(pet -> new AdminPetResponse(
                        pet.getId(),
                        pet.getName(),
                        pet.getSpecies(),
                        pet.getBreed(),
                        pet.getListingType(),
                        pet.getStatus(),
                        pet.getPrice(),
                        pet.getOwner().getFullName()
                ))
                .toList();
    }

    public void deletePet(String requesterEmail, Long petId) {
        ensureAdmin(requesterEmail);
        requireModerationDependencies();

        if (!petListingRepository.existsById(petId)) {
            throw new IllegalArgumentException("Pet listing not found");
        }

        petListingRepository.deleteById(petId);
    }

    public List<AdminModerationUserResponse> getAllUsers(String requesterEmail) {
        ensureAdmin(requesterEmail);
        requireModerationDependencies();

        return userRepository.findAll()
                .stream()
                .map(user -> new AdminModerationUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole(),
                        user.isSuspended(),
                        user.getCreatedAt(),
                        purchaseRepository.countByBuyer(user),
                        tradeOfferRepository.countByOfferingUser(user)
                ))
                .toList();
    }

    public AdminModerationUserResponse suspendUser(String requesterEmail, Long userId) {
        User admin = ensureAdmin(requesterEmail);
        requireModerationDependencies();

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (target.getId().equals(admin.getId())) {
            throw new IllegalArgumentException("You cannot suspend your own admin account");
        }

        target.setSuspended(true);
        userRepository.save(target);

        return new AdminModerationUserResponse(
                target.getId(),
                target.getFullName(),
                target.getEmail(),
                target.getRole(),
                target.isSuspended(),
                target.getCreatedAt(),
                purchaseRepository.countByBuyer(target),
                tradeOfferRepository.countByOfferingUser(target)
        );
    }

    private User ensureAdmin(String requesterEmail) {
        User requester = userRepository.findByEmailIgnoreCase(normalizeEmail(requesterEmail))
                .orElseThrow(() -> new IllegalArgumentException("Requester account not found"));

        if (!"ADMIN".equalsIgnoreCase(requester.getRole())) {
            throw new SecurityException("Only ADMIN accounts can perform this action");
        }
        return requester;
    }

    private void requireModerationDependencies() {
        if (petListingRepository == null || purchaseRepository == null || tradeOfferRepository == null) {
            throw new IllegalStateException("Admin moderation dependencies are not configured");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
