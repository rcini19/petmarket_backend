package com.dev.petmarket_backend.login.service;

import com.dev.petmarket_backend.common.dto.AuthResponse;
import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.common.security.JwtUtil;
import com.dev.petmarket_backend.login.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.isSuspended()) {
            throw new IllegalArgumentException("This account is suspended. Contact support.");
        }

        String selectedRole = request.getLoginAs() == null
                ? ""
                : request.getLoginAs().trim().toUpperCase(Locale.ROOT);

        if (!"USER".equals(selectedRole) && !"ADMIN".equals(selectedRole)) {
            throw new IllegalArgumentException("Invalid role selected");
        }

        String accountRole = user.getRole() == null
                ? "USER"
                : user.getRole().trim().toUpperCase(Locale.ROOT);

        if (!accountRole.equals(selectedRole)) {
            throw new IllegalArgumentException("Selected role does not match this account");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole(), "Login successful");
    }
}
