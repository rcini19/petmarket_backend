package com.dev.petmarket_backend.admin.service;

import com.dev.petmarket_backend.admin.dto.AdminUserResponse;
import com.dev.petmarket_backend.admin.dto.CreateAdminRequest;
import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUserResponse createAdmin(String requesterEmail, CreateAdminRequest request) {
        User requester = userRepository.findByEmailIgnoreCase(normalizeEmail(requesterEmail))
                .orElseThrow(() -> new IllegalArgumentException("Requester account not found"));

        if (!"ADMIN".equalsIgnoreCase(requester.getRole())) {
            throw new SecurityException("Only ADMIN accounts can create another admin account");
        }

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

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
