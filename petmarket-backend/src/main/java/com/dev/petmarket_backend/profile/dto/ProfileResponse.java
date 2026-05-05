package com.dev.petmarket_backend.profile.dto;

import java.time.LocalDateTime;

public class ProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String accountType;
    private LocalDateTime memberSince;
    private String profileImageUrl;
    private String token;

    public ProfileResponse(Long id,
                           String fullName,
                           String email,
                           String role,
                           String accountType,
                           LocalDateTime memberSince,
                           String profileImageUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.accountType = accountType;
        this.memberSince = memberSince;
        this.profileImageUrl = profileImageUrl;
        this.token = null;
    }

    public ProfileResponse(String fullName,
                           String email,
                           String role,
                           String accountType,
                           LocalDateTime memberSince,
                           String profileImageUrl) {
        this(null, fullName, email, role, accountType, memberSince, profileImageUrl);
    }

    public ProfileResponse(Long id,
                           String fullName,
                           String email,
                           String role,
                           String accountType,
                           LocalDateTime memberSince,
                           String profileImageUrl,
                           String token) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.accountType = accountType;
        this.memberSince = memberSince;
        this.profileImageUrl = profileImageUrl;
        this.token = token;
    }

    public ProfileResponse(String fullName,
                           String email,
                           String role,
                           String accountType,
                           LocalDateTime memberSince,
                           String profileImageUrl,
                           String token) {
        this(null, fullName, email, role, accountType, memberSince, profileImageUrl, token);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public LocalDateTime getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDateTime memberSince) {
        this.memberSince = memberSince;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
