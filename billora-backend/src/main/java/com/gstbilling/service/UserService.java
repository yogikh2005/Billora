package com.gstbilling.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gstbilling.dao.BusinessDetailsRepository;
import com.gstbilling.dao.UserRepository;
import com.gstbilling.dto.CreateWorkerDTO;
import com.gstbilling.dto.OwnerProfileResponse;
import com.gstbilling.dto.OwnerProfileUpdateRequest;
import com.gstbilling.dto.UpdateWorkerBusinessesDTO;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessDetailsRepository businessDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // =====================================================
    // Get All Users
    // =====================================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =====================================================
    // Get User By ID
    // =====================================================

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        )
                );
    }


    // =====================================================
    // CREATE WORKER
    // Owner creates worker + assigns businesses
    // =====================================================

    @Transactional
    public User createWorker(
            CreateWorkerDTO dto,
            String ownerUsername) {

        // ==========================================
        // 1. Find logged-in owner
        // ==========================================

        User owner = userRepository.findByUsername(ownerUsername);
         if(owner==null)
               new RuntimeException("Owner not found");
             


        // ==========================================
        // 2. Verify owner
        // ==========================================

        if (owner.getRole() != Role.ROLE_OWNER) {

            throw new RuntimeException(
                    "Only owner can create workers"
            );
        }


        // ==========================================
        // 3. Check username
        // ==========================================

        if (userRepository
                .existsByUsername(dto.getUsername())) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }


        // ==========================================
        // 4. Check email
        // ==========================================

        if (userRepository
                .existsByEmail(dto.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }


        // ==========================================
        // 5. Create worker
        // ==========================================

        User worker = User.builder()

                .username(dto.getUsername())

                .email(dto.getEmail())

                .password(
                        passwordEncoder.encode(
                                dto.getPassword()
                        )
                )

                // NEVER accept role from React
                .role(Role.ROLE_USER)

                .active(dto.getActive() != null
                        ? dto.getActive()
                                : true
                        )

                // Worker belongs to this owner
                .owner(owner)

                .build();


        // ==========================================
        // 6. Find businesses
        // ==========================================

        List<BusinessDetails> businesses =
                businessDetailsRepository.findAllById(
                        dto.getBusinessIds()
                );
        
        for (BusinessDetails business : businesses) {

            if (!business.isActive()) {
                throw new IllegalStateException(
                        "Cannot assign inactive business: "
                        + business.getBusinessName()
                );
            }
        }

        // ==========================================
        // 7. Verify all businesses exist
        // ==========================================

        if (businesses.size()
                != dto.getBusinessIds().size()) {

            throw new RuntimeException(
                    "One or more businesses not found"
            );
        }


        // ==========================================
        // 8. Verify owner owns businesses
        // ==========================================

        for (BusinessDetails business : businesses) {

            if (!business.getOwner()
                    .getId()
                    .equals(owner.getId())) {

                throw new RuntimeException(
                        "You cannot assign a worker to " +
                        "a business you do not own"
                );
            }
        }


        // ==========================================
        // 9. ASSIGN WORKER TO BUSINESSES
        // ==========================================

        // BusinessDetails is the owning side
        for (BusinessDetails business : businesses) {

            business.addWorker(worker);
        }


        // ==========================================
        // 10. Save worker
        // ==========================================

        User savedWorker =
                userRepository.save(worker);


        // ==========================================
        // 11. Save owning side
        // ==========================================

        businessDetailsRepository.saveAll(
                businesses
        );


        return savedWorker;
    }

    // =====================================================
    // UPDATE WORKER BUSINESS ASSIGNMENTS
    // =====================================================

    @Transactional
    public User updateWorkerBusinesses(
            Long workerId,
            UpdateWorkerBusinessesDTO dto,
            String ownerUsername) {

        // ==========================================
        // 1. Find owner
        // ==========================================

        User owner = userRepository.findByUsername(ownerUsername);
        if(owner==null)
              new RuntimeException("Owner not found");


        // ==========================================
        // 2. Find worker
        // ==========================================

        User worker = userRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Worker not found"
                        )
                );


        // ==========================================
        // 3. Verify worker
        // ==========================================

        if (worker.getRole() != Role.ROLE_USER) {

            throw new RuntimeException(
                    "Selected user is not a worker"
            );
        }


        // ==========================================
        // 4. Verify worker belongs to owner
        // ==========================================

        if (worker.getOwner() == null ||
                !worker.getOwner()
                        .getId()
                        .equals(owner.getId())) {

            throw new RuntimeException(
                    "You cannot modify this worker"
            );
        }


        // ==========================================
        // 5. Find new businesses
        // ==========================================

        List<BusinessDetails> newBusinesses =
                businessDetailsRepository
                        .findAllById(
                                dto.getBusinessIds()
                        );


     // ==========================================
     // 7. Verify owner and business status
     // ==========================================

     for (BusinessDetails business : newBusinesses) {

         // Owner check
         if (!business.getOwner()
                 .getId()
                 .equals(owner.getId())) {

             throw new RuntimeException(
                     "You cannot assign this business: "
                     + business.getBusinessName()
             );
         }

         // Active check
         if (!business.isActive()) {

             throw new IllegalStateException(
                     "Cannot assign inactive business: "
                     + business.getBusinessName()
             );
         }
     }

        // ==========================================
        // 8. Remove OLD assignments
        // ==========================================

        Set<BusinessDetails> oldBusinesses =
                new HashSet<>(
                        worker.getAssignedBusinesses()
                );


        for (BusinessDetails business :
                oldBusinesses) {

            business.removeWorker(worker);
        }


        // ==========================================
        // 9. Add NEW assignments
        // ==========================================

        for (BusinessDetails business :
                newBusinesses) {

            business.addWorker(worker);
        }


        // ==========================================
        // 10. Save
        // ==========================================
       
        userRepository.save(worker);

        businessDetailsRepository.saveAll(
                newBusinesses
        );


        return worker;
    }
    // =====================================================
    // Update User
    // =====================================================

    public User updateUser(
            Long id,
            User request) {

        User existing = getUserById(id);

        existing.setUsername(
                request.getUsername()
        );

        existing.setEmail(
                request.getEmail()
        );

        existing.setActive(request.isActive());

        return userRepository.save(existing);
    }


    // =====================================================
    // Delete User
    // =====================================================

    public void deleteUser(Long id) {

        User user = getUserById(id);
        if(user==null)
             new RuntimeException("User not found");

            user.setActive(false);

            userRepository.save(user);
        }
        //userRepository.delete(user);
    
    
    public void activateUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setActive(true);

        userRepository.save(user);
    }
    
    
    //
    //
    
    public List<User> getWorkersByOwner(
            String ownerUsername) {

        
        User owner = userRepository.findByUsername(ownerUsername);
        if(owner==null)
              new RuntimeException("Owner not found");

        return userRepository.findByOwnerId(
                owner.getId()
        );
    }


	public OwnerProfileResponse getOwnerProfile(String username) {

    User user = userRepository.findByUsername(username);
    if(user==null)
        throw  new RuntimeException("Owner not found");
    System.out.println("pass"+user.getPassword()); 
    System.out.println(passwordEncoder.encode(user.getPassword()));
   
    return OwnerProfileResponse.builder()
    		.username(user.getUsername())
    		.email(user.getEmail())
    		.password(user.getPassword())
    		.build();
    		
  
	}
	
	@Transactional
	public OwnerProfileResponse updateOwnerProfile(
	        String username,
	        OwnerProfileUpdateRequest request) {

		User user = userRepository.findByUsername(username);

		if (user == null) {
		    throw new RuntimeException("User not found");
		}

	    user.setUsername(request.getUsername());
	    user.setEmail(request.getEmail());
	    user.setPassword(passwordEncoder.encode(request.getPassword()));

	    User updatedUser = userRepository.save(user);

	     return OwnerProfileResponse.builder()
	    		.username(updatedUser.getUsername())
	    		.email(updatedUser.getEmail())
	    		.password(updatedUser.getPassword())
	    		.build();
	}
}