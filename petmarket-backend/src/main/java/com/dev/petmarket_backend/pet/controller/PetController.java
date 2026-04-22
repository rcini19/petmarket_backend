package com.dev.petmarket_backend.pet.controller;

import com.dev.petmarket_backend.common.dto.ErrorResponse;
import com.dev.petmarket_backend.pet.dto.PetRequest;
import com.dev.petmarket_backend.pet.dto.PetResponse;
import com.dev.petmarket_backend.pet.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/pets", "/pets"})
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getPets(@RequestParam(required = false) String search,
                                                     @RequestParam(required = false) String listingType,
                                                     @RequestParam(required = false) String status) {
        return ResponseEntity.ok(petService.getPets(search, listingType, status));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PetResponse>> getMyPets(Authentication authentication) {
        return ResponseEntity.ok(petService.getMyPets(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPet(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(petService.getPetById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPet(Authentication authentication,
                                       @Valid @RequestBody PetRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(authentication.getName(), request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(Authentication authentication,
                                       @PathVariable Long id,
                                       @Valid @RequestBody PetRequest request) {
        try {
            return ResponseEntity.ok(petService.updatePet(authentication.getName(), id, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(Authentication authentication, @PathVariable Long id) {
        try {
            petService.deletePet(authentication.getName(), id);
            return ResponseEntity.ok(Map.of("message", "Listing deleted successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
