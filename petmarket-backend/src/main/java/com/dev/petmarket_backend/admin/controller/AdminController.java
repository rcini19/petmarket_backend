package com.dev.petmarket_backend.admin.controller;

import com.dev.petmarket_backend.admin.dto.CreateAdminRequest;
import com.dev.petmarket_backend.admin.service.AdminService;
import com.dev.petmarket_backend.common.dto.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/admin", "/admin"})
public class AdminController {

    private final AdminService adminService;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

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
    public ResponseEntity<?> getAllPets(Authentication authentication,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int pageSize) {
        try {
            // Validate pagination parameters
            if (page < 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Page number must be >= 0"));
            }
            if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Page size must be between 1 and " + MAX_PAGE_SIZE)
                );
            }

            return ResponseEntity.ok(
                    adminService.getPetsWithPagination(authentication.getName(), page, pageSize)
            );
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
    public ResponseEntity<?> getAllUsers(Authentication authentication,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int pageSize) {
        try {
            // Validate pagination parameters
            if (page < 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Page number must be >= 0"));
            }
            if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("Page size must be between 1 and " + MAX_PAGE_SIZE)
                );
            }

            return ResponseEntity.ok(
                    adminService.getUsersWithPagination(authentication.getName(), page, pageSize)
            );
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

