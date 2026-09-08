package com.gstbilling.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.User;

public interface BusinessDetailsRepository
        extends JpaRepository<BusinessDetails, Long> {

    boolean existsByGstin(String gstin);

    Optional<BusinessDetails> findByGstin(String gstin);

    Optional<BusinessDetails> findByBusinessName(
            String businessName
    );

    List<BusinessDetails> findByOwnerId(Long ownerId);
    
    

	List<BusinessDetails> findByOwner(User owner);

}