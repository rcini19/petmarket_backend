package com.dev.petmarket_backend.purchase.repository;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.purchase.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    long countByBuyer(User buyer);
    boolean existsByPet_Id(Long petId);
    List<Purchase> findByBuyerOrderByCreatedAtDesc(User buyer);
}
