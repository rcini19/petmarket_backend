package com.dev.petmarket_backend.profile.controller;

import com.dev.petmarket_backend.common.dto.ErrorResponse;
import com.dev.petmarket_backend.profile.dto.ChangePasswordRequest;
import com.dev.petmarket_backend.profile.dto.HistoryItemResponse;
import com.dev.petmarket_backend.profile.dto.PhotoUploadResponse;
import com.dev.petmarket_backend.profile.dto.ProfileResponse;
import com.dev.petmarket_backend.profile.dto.UpdatePhotoRequest;
import com.dev.petmarket_backend.profile.dto.UpdateProfileRequest;
import com.dev.petmarket_backend.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/profile", "/profile"})
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            ProfileResponse response = profileService.getProfile(authentication.getName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody UpdateProfileRequest request) {
        try {
            ProfileResponse response = profileService.updateProfile(authentication.getName(), request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            profileService.changePassword(authentication.getName(), request);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping(value = "/me/photo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePhoto(Authentication authentication,
                                         @RequestBody UpdatePhotoRequest request) {
        try {
            PhotoUploadResponse response = profileService.updateProfilePhoto(authentication.getName(), request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePhotoMultipart(Authentication authentication,
                                                  MultipartHttpServletRequest request) {
        try {
            MultipartFile file = extractFirstUploadedFile(request);
            PhotoUploadResponse response = profileService.updateProfilePhoto(authentication.getName(), file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhotoMultipart(Authentication authentication,
                                                  MultipartHttpServletRequest request) {
        try {
            MultipartFile file = extractFirstUploadedFile(request);
            PhotoUploadResponse response = profileService.updateProfilePhoto(authentication.getName(), file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    private MultipartFile extractFirstUploadedFile(MultipartHttpServletRequest request) {
        if (request == null || request.getFileMap().isEmpty()) {
            return null;
        }

        for (MultipartFile file : request.getFileMap().values()) {
            if (file != null && !file.isEmpty()) {
                return file;
            }
        }

        return request.getFileMap().values().stream().findFirst().orElse(null);
    }

    @GetMapping("/me/orders")
    public ResponseEntity<List<HistoryItemResponse>> getOrderHistory(Authentication authentication) {
        return ResponseEntity.ok(profileService.getOrderHistory(authentication.getName()));
    }

    @GetMapping("/me/trades")
    public ResponseEntity<List<HistoryItemResponse>> getTradeHistory(Authentication authentication) {
        return ResponseEntity.ok(profileService.getTradeHistory(authentication.getName()));
    }
}
