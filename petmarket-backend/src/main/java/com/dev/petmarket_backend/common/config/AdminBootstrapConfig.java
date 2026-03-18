package com.dev.petmarket_backend.common.config;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    public CommandLineRunner createDefaultAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.enabled:true}") boolean enabled,
            @Value("${app.bootstrap.admin.email:admin@petmarket.com}") String adminEmail,
            @Value("${app.bootstrap.admin.password:Admin@12345}") String adminPassword,
            @Value("${app.bootstrap.admin.full-name:PetMarket Admin}") String adminFullName) {
        return args -> {
            if (!enabled) {
                return;
            }

            String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                return;
            }

            User admin = new User();
            admin.setEmail(normalizedEmail);
            admin.setFullName(adminFullName.trim());
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        };
    }
}
