package com.dev.petmarket_backend.profile.service;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.common.security.JwtUtil;
import com.dev.petmarket_backend.pet.model.PetListing;
import com.dev.petmarket_backend.profile.dto.ChangePasswordRequest;
import com.dev.petmarket_backend.profile.dto.HistoryItemResponse;
import com.dev.petmarket_backend.profile.dto.PhotoUploadResponse;
import com.dev.petmarket_backend.profile.dto.ProfileResponse;
import com.dev.petmarket_backend.profile.dto.UpdatePhotoRequest;
import com.dev.petmarket_backend.profile.dto.UpdateProfileRequest;
import com.dev.petmarket_backend.purchase.model.Purchase;
import com.dev.petmarket_backend.purchase.repository.PurchaseRepository;
import com.dev.petmarket_backend.trade.model.TradeOffer;
import com.dev.petmarket_backend.trade.repository.TradeOfferRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private static final int MAX_PROFILE_IMAGE_BYTES = 2 * 1024 * 1024;
    private static final Pattern IMAGE_DATA_URL_PATTERN =
            Pattern.compile("^data:image/(png|jpe?g);base64,([A-Za-z0-9+/=\\r\\n]+)$", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PurchaseRepository purchaseRepository;
    private final TradeOfferRepository tradeOfferRepository;

    public ProfileService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          PurchaseRepository purchaseRepository,
                          TradeOfferRepository tradeOfferRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.purchaseRepository = purchaseRepository;
        this.tradeOfferRepository = tradeOfferRepository;
    }

    public ProfileResponse getProfile(String email) {
        User user = getUserByEmail(email);
        return toProfileResponse(user);
    }

    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserByEmail(email);

        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String fullName = request.getFullName().trim();

        if (fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, user.getId())) {
            throw new IllegalArgumentException("This email is already used by another account");
        }

        user.setFullName(fullName);
        user.setEmail(normalizedEmail);
        userRepository.save(user);

        ProfileResponse response = toProfileResponse(user);
        response.setToken(jwtUtil.generateToken(user.getEmail(), user.getRole()));
        return response;
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getUserByEmail(email);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from your current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public PhotoUploadResponse updateProfilePhoto(String email, UpdatePhotoRequest request) {
        User user = getUserByEmail(email);

        String profileImageUrl = request == null ? null : request.getProfileImageUrl();
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            user.setProfileImageData(null);
            user.setProfileImageContentType(null);
            userRepository.save(user);
            return new PhotoUploadResponse(
                    "Profile photo removed successfully",
                    buildFileReference(user),
                    toProfileResponse(user)
            );
        }

        Matcher matcher = IMAGE_DATA_URL_PATTERN.matcher(profileImageUrl.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Only JPG/JPEG and PNG images are supported");
        }

        String fileExtension = matcher.group(1).toLowerCase(Locale.ROOT);
        String contentType = fileExtension.startsWith("jp") ? "image/jpeg" : "image/png";

        byte[] imageBytes;
        try {
            imageBytes = Base64.getMimeDecoder().decode(matcher.group(2));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid image encoding");
        }

        if (imageBytes.length > MAX_PROFILE_IMAGE_BYTES) {
            throw new IllegalArgumentException("Profile image is too large");
        }

        user.setProfileImageData(imageBytes);
        user.setProfileImageContentType(contentType);
        userRepository.save(user);

        return new PhotoUploadResponse(
                "Profile photo uploaded successfully",
                buildFileReference(user),
                toProfileResponse(user)
        );
    }

    public PhotoUploadResponse updateProfilePhoto(String email, MultipartFile file) {
        User user = getUserByEmail(email);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image file to upload");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Unsupported image type");
        }

        String normalizedContentType = contentType.toLowerCase(Locale.ROOT);
        if (!"image/jpeg".equals(normalizedContentType) && !"image/png".equals(normalizedContentType)) {
            throw new IllegalArgumentException("Only JPG/JPEG and PNG images are supported");
        }

        if (file.getSize() > MAX_PROFILE_IMAGE_BYTES) {
            throw new IllegalArgumentException("Profile image is too large");
        }

        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read uploaded image");
        }

        user.setProfileImageData(imageBytes);
        user.setProfileImageContentType(normalizedContentType);
        userRepository.save(user);

        return new PhotoUploadResponse(
                "Profile photo uploaded successfully",
                buildFileReference(user),
                toProfileResponse(user)
        );
    }

    public List<HistoryItemResponse> getOrderHistory(String email) {
        User user = getUserByEmail(email);
        List<Purchase> purchases = purchaseRepository.findByBuyerOrderByCreatedAtDesc(user);

        return purchases.stream().map(purchase -> {
            PetListing pet = purchase.getPet();
            return new HistoryItemResponse(
                    String.valueOf(purchase.getId()),
                    pet.getName(),
                    purchase.getCreatedAt().format(DATE_FORMATTER),
                    pet.getBreed() + " • " + pet.getSpecies(),
                    "COMPLETED",
                    purchase.getTotalPrice().doubleValue()
            );
        }).collect(Collectors.toList());
    }

    public List<HistoryItemResponse> getTradeHistory(String email) {
        User user = getUserByEmail(email);
        List<TradeOffer> trades = tradeOfferRepository.findAcceptedTradesForUser(user);

        return trades.stream().map(trade -> {
            PetListing offeredPet = trade.getOfferedPet();
            PetListing requestedPet = trade.getRequestedPet();
            boolean wasOfferingUser = trade.getOfferingUser().getId().equals(user.getId());
            PetListing receivedPet = wasOfferingUser ? requestedPet : offeredPet;
            PetListing givenPet = wasOfferingUser ? offeredPet : requestedPet;

            return new HistoryItemResponse(
                    String.valueOf(trade.getId()),
                    "Received: " + receivedPet.getName(),
                    trade.getRespondedAt() != null ? trade.getRespondedAt().format(DATE_FORMATTER) : trade.getCreatedAt().format(DATE_FORMATTER),
                    "Traded: " + givenPet.getName() + " • " + receivedPet.getBreed(),
                    "ACCEPTED",
                    0.0
            );
        }).collect(Collectors.toList());
    }

    private User getUserByEmail(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }

    private String buildProfileImageDataUrl(User user) {
        byte[] imageBytes = user.getProfileImageData();
        String contentType = user.getProfileImageContentType();

        if (imageBytes == null || imageBytes.length == 0 || contentType == null || contentType.isBlank()) {
            return null;
        }

        String encoded = Base64.getEncoder().encodeToString(imageBytes);
        return "data:" + contentType + ";base64," + encoded;
    }

    private String buildFileReference(User user) {
        return "users/" + user.getId() + "/profile-photo";
    }

    private ProfileResponse toProfileResponse(User user) {
        String role = user.getRole() == null ? "USER" : user.getRole();
        String accountType = "ADMIN".equalsIgnoreCase(role) ? "Administrator" : "Member";

        return new ProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                role,
                accountType,
                user.getCreatedAt(),
                buildProfileImageDataUrl(user)
        );
    }
}
