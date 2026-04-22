package com.dev.petmarket_backend.admin.controller;

import com.dev.petmarket_backend.admin.dto.CreateAdminRequest;
import com.dev.petmarket_backend.admin.service.AdminService;
import com.dev.petmarket_backend.common.dto.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping({"/api/admin", "/admin"})
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/users/admin")
    public ResponseEntity<?> createAdmin(Authentication authentication,
                                         @Valid @RequestBody CreateAdminRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(adminService.createAdmin(authentication.getName(), request));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/pets")
    public ResponseEntity<?> getAllPets(Authentication authentication) {
        try {
            return ResponseEntity.ok(adminService.getAllPets(authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<?> deletePet(Authentication authentication, @PathVariable Long id) {
        try {
            adminService.deletePet(authentication.getName(), id);
            return ResponseEntity.ok(Map.of("message", "Listing removed by admin"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            return ResponseEntity.ok(adminService.getAllUsers(authentication.getName()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<?> suspendUser(Authentication authentication, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.suspendUser(authentication.getName(), id));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
