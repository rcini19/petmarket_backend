package com.dev.petmarket_backend.trade.service;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.common.repository.UserRepository;
import com.dev.petmarket_backend.pet.model.PetListing;
import com.dev.petmarket_backend.pet.repository.PetListingRepository;
import com.dev.petmarket_backend.trade.dto.TradeRequest;
import com.dev.petmarket_backend.trade.dto.TradeResponse;
import com.dev.petmarket_backend.trade.model.TradeOffer;
import com.dev.petmarket_backend.trade.repository.TradeOfferRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {

    private final TradeOfferRepository tradeOfferRepository;
    private final PetListingRepository petListingRepository;
    private final UserRepository userRepository;

    public TradeService(TradeOfferRepository tradeOfferRepository,
                        PetListingRepository petListingRepository,
                        UserRepository userRepository) {
        this.tradeOfferRepository = tradeOfferRepository;
        this.petListingRepository = petListingRepository;
        this.userRepository = userRepository;
    }

    public TradeResponse createTrade(String requesterEmail, TradeRequest request) {
        User offeringUser = userRepository.findByEmailIgnoreCase(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Offering user not found"));

        PetListing offeredPet = petListingRepository.findById(request.getOfferedPetId())
                .orElseThrow(() -> new IllegalArgumentException("Offered pet not found"));

        PetListing requestedPet = petListingRepository.findById(request.getRequestedPetId())
                .orElseThrow(() -> new IllegalArgumentException("Requested pet not found"));

        if (!offeredPet.getOwner().getId().equals(offeringUser.getId())) {
            throw new SecurityException("You may only offer a pet that you own");
        }

        if (requestedPet.getOwner().getId().equals(offeringUser.getId())) {
            throw new IllegalArgumentException("You cannot trade for your own pet");
        }

        if (!isTradeType(requestedPet.getListingType())) {
            throw new IllegalArgumentException("Requested pet must be marked for Trade");
        }

        if (!"AVAILABLE".equalsIgnoreCase(offeredPet.getStatus()) || !"AVAILABLE".equalsIgnoreCase(requestedPet.getStatus())) {
            throw new IllegalArgumentException("Both pets must be available for trade");
        }

        TradeOffer tradeOffer = new TradeOffer();
        tradeOffer.setOfferedPet(offeredPet);
        tradeOffer.setRequestedPet(requestedPet);
        tradeOffer.setOfferingUser(offeringUser);
        tradeOffer.setStatus("PENDING");

        return toResponse(tradeOfferRepository.save(tradeOffer));
    }

    public TradeResponse acceptTrade(String requesterEmail, Long tradeId) {
        TradeOffer trade = getTrade(tradeId);
        PetListing requestedPet = trade.getRequestedPet();
        PetListing offeredPet = trade.getOfferedPet();

        User requester = userRepository.findByEmailIgnoreCase(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester account not found"));

        if (!requestedPet.getOwner().getId().equals(requester.getId())) {
            throw new SecurityException("Only pet owner may accept this trade");
        }

        if (!"PENDING".equalsIgnoreCase(trade.getStatus())) {
            throw new IllegalArgumentException("Trade offer is no longer pending");
        }

        if (!"AVAILABLE".equalsIgnoreCase(requestedPet.getStatus()) || !"AVAILABLE".equalsIgnoreCase(offeredPet.getStatus())) {
            throw new IllegalArgumentException("One or both pets are no longer available for trade");
        }

        User requestedOwner = requestedPet.getOwner();
        User offeredOwner = offeredPet.getOwner();

        requestedPet.setOwner(offeredOwner);
        offeredPet.setOwner(requestedOwner);

        // Mark both pets as TRADED so they cannot be purchased or traded again
        requestedPet.setStatus("TRADED");
        offeredPet.setStatus("TRADED");

        petListingRepository.save(requestedPet);
        petListingRepository.save(offeredPet);

        trade.setStatus("ACCEPTED");
        trade.setRespondedAt(LocalDateTime.now());

        return toResponse(tradeOfferRepository.save(trade));
    }

    public TradeResponse rejectTrade(String requesterEmail, Long tradeId) {
        TradeOffer trade = getTrade(tradeId);
        PetListing requestedPet = trade.getRequestedPet();

        User requester = userRepository.findByEmailIgnoreCase(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester account not found"));

        if (!requestedPet.getOwner().getId().equals(requester.getId())) {
            throw new SecurityException("Only pet owner may reject this trade");
        }

        if (!"PENDING".equalsIgnoreCase(trade.getStatus())) {
            throw new IllegalArgumentException("Trade offer is no longer pending");
        }

        trade.setStatus("REJECTED");
        trade.setRespondedAt(LocalDateTime.now());

        return toResponse(tradeOfferRepository.save(trade));
    }

    public List<TradeResponse> getTrades() {
        return tradeOfferRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TradeOffer getTrade(Long tradeId) {
        return tradeOfferRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade offer not found"));
    }

    private boolean isTradeType(String listingType) {
        if (listingType == null) {
            return false;
        }
        String normalized = listingType.toUpperCase();
        return "TRADE".equals(normalized) || "BOTH".equals(normalized);
    }

    private TradeResponse toResponse(TradeOffer trade) {
        return new TradeResponse(
                trade.getId(),
                trade.getOfferedPet().getId(),
                trade.getOfferedPet().getName(),
                trade.getRequestedPet().getId(),
                trade.getRequestedPet().getName(),
                trade.getOfferingUser().getId(),
                trade.getOfferingUser().getFullName(),
                trade.getStatus(),
                trade.getCreatedAt()
        );
    }
}
