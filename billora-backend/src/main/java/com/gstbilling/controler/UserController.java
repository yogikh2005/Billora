package com.gstbilling.controler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.gstbilling.dto.BusinessSummaryDTO;
import com.gstbilling.dto.CreateWorkerDTO;
import com.gstbilling.dto.OwnerProfileResponse;
import com.gstbilling.dto.OwnerProfileUpdateRequest;
import com.gstbilling.dto.UpdateWorkerBusinessesDTO;
import com.gstbilling.dto.UserDTO;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.User;
import com.gstbilling.service.UserService;

@RestController
@RequestMapping("/owner/users")
@PreAuthorize("hasRole('OWNER')")
public class UserController {

    @Autowired
    private UserService userService;


    // ==========================================
    // GET USERS
    // ==========================================

    @GetMapping
    public List<UserDTO> getAllUsers(
            Authentication authentication) {

        String ownerUsername =
                authentication.getName();

        return userService
                .getWorkersByOwner(ownerUsername)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    // ==========================================
    // GET USER
    // ==========================================

    @GetMapping("/{id}")
    public UserDTO getUser(
            @PathVariable Long id) {

        return mapToDTO(
                userService.getUserById(id)
        );
    }


    // ==========================================
    // CREATE WORKER
    // ==========================================

    @PostMapping
    public UserDTO createUser(
            @RequestBody CreateWorkerDTO request,
            Authentication authentication) {

        String ownerUsername =
                authentication.getName();

        User worker =
                userService.createWorker(
                        request,
                        ownerUsername
                );
        

        return mapToDTO(worker);
    }


    // ==========================================
    // UPDATE WORKER BUSINESSES
    // ==========================================

    @PutMapping("/{id}/businesses")
    public UserDTO updateWorkerBusinesses(
            @PathVariable Long id,
            @RequestBody UpdateWorkerBusinessesDTO request,
            Authentication authentication) {

        String ownerUsername =
                authentication.getName();

        User worker =
                userService.updateWorkerBusinesses(
                        id,
                        request,
                        ownerUsername
                );

        return mapToDTO(worker);
    }


    // ==========================================
    // UPDATE WORKER
    // ==========================================

    @PutMapping("/{id}")
    public UserDTO updateUser(
            @PathVariable Long id,
            @RequestBody User request) {

        return mapToDTO(
                userService.updateUser(
                        id,
                        request
                )
        );
    }


    // ==========================================
    // DELETE
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                "User deleted successfully!"
        );
    }


    // ==========================================
    // DTO
    // ==========================================

    private UserDTO mapToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());

        dto.setUsername(
                user.getUsername()
        );

        dto.setEmail(
                user.getEmail()
        );

        dto.setRole(
                user.getRole().name()
        );

        dto.setActive(
                user.isActive()
        );

        dto.setCreatedAt(
                user.getCreatedAt()
        );

        dto.setUpdatedAt(
                user.getUpdatedAt()
        );


        Set<BusinessSummaryDTO> businesses =
                user.getAssignedBusinesses()
                        .stream()
                        .map(business -> {

                            BusinessSummaryDTO b =
                                    new BusinessSummaryDTO();

                            b.setId(
                                    business.getId()
                            );

                            b.setBusinessName(
                                    business.getBusinessName()
                            );

                            return b;

                        })
                        .collect(
                                Collectors.toSet()
                        );


        dto.setBusinesses(businesses);


        return dto;
    }
    
    @RestController
    @RequestMapping("/owner/profile")
    @PreAuthorize("hasRole('OWNER')")
    public class OwnerProfileController {

        @Autowired
        private UserService userService;

        @GetMapping
        public ResponseEntity<OwnerProfileResponse> getProfile(
                Authentication authentication) {

            String username = authentication.getName();

            return ResponseEntity.ok(
                    userService.getOwnerProfile(username)
            );
        }

        @PutMapping
        public ResponseEntity<OwnerProfileResponse> updateProfile(
                Authentication authentication,
                @RequestBody OwnerProfileUpdateRequest request) {

            String username = authentication.getName();

            return ResponseEntity.ok(
                    userService.updateOwnerProfile(username, request)
            );
        }
    }
    
    
    
}