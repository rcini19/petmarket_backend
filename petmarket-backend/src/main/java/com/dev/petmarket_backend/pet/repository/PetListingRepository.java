package com.dev.petmarket_backend.pet.repository;

import com.dev.petmarket_backend.common.model.User;
import com.dev.petmarket_backend.pet.model.PetListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PetListingRepository extends JpaRepository<PetListing, Long>, JpaSpecificationExecutor<PetListing> {
    List<PetListing> findByOwnerOrderByCreatedAtDesc(User owner);

    List<PetListing> findByOwnerAndStatusOrderByCreatedAtDesc(User owner, String status);

    Page<PetListing> findByOwner(User owner, Pageable pageable);

    Page<PetListing> findByOwnerAndStatus(User owner, String status, Pageable pageable);
}
