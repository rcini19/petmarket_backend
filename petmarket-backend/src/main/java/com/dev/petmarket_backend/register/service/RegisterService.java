package com.dev.petmarket_backend.register.service;

import com.dev.petmarket_backend.common.dto.AuthResponse;
import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.common.security.JwtUtil;
import com.dev.petmarket_backend.register.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public RegisterService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        // Validate and normalize role; default to USER if not provided or invalid
        String userRole = "USER";
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            String normalizedRole = request.getRole().trim().toUpperCase();
            if ("ADMIN".equals(normalizedRole) || "USER".equals(normalizedRole)) {
                userRole = normalizedRole;
            } else {
                throw new IllegalArgumentException("Invalid role. Allowed values: USER, ADMIN");
            }
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(userRole);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), "Registration successful");
    }
}
