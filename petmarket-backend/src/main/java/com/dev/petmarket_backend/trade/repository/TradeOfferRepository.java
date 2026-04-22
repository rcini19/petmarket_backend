package com.dev.petmarket_backend.trade.repository;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.trade.model.TradeOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TradeOfferRepository extends JpaRepository<TradeOffer, Long> {
    List<TradeOffer> findByOfferingUserOrderByCreatedAtDesc(User offeringUser);
    long countByOfferingUser(User offeringUser);

    @Query("SELECT t FROM TradeOffer t WHERE (t.offeringUser = :user OR t.offeredPet.owner = :user OR t.requestedPet.owner = :user) AND t.status = 'ACCEPTED' ORDER BY t.respondedAt DESC")
    List<TradeOffer> findAcceptedTradesForUser(@Param("user") User user);

    boolean existsByOfferedPet_IdAndStatus(Long petId, String status);
    boolean existsByRequestedPet_IdAndStatus(Long petId, String status);
}
