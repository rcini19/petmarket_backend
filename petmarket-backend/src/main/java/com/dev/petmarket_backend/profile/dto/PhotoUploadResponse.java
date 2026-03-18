package com.dev.petmarket_backend.profile.dto;

public class PhotoUploadResponse {

    private String message;
    private String fileReference;
    private ProfileResponse profile;

    public PhotoUploadResponse(String message, String fileReference, ProfileResponse profile) {
        this.message = message;
        this.fileReference = fileReference;
        this.profile = profile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public ProfileResponse getProfile() {
        return profile;
    }

    public void setProfile(ProfileResponse profile) {
        this.profile = profile;
    }
}