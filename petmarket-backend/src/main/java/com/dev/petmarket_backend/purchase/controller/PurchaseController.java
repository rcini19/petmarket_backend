package com.dev.petmarket_backend.purchase.controller;

import com.dev.petmarket_backend.common.dto.ErrorResponse;
import com.dev.petmarket_backend.purchase.dto.PurchaseRequest;
import com.dev.petmarket_backend.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/purchases", "/purchases"})
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<?> createPurchase(Authentication authentication,
                                            @Valid @RequestBody PurchaseRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(purchaseService.createPurchase(authentication.getName(), request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchase(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchase(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
}
