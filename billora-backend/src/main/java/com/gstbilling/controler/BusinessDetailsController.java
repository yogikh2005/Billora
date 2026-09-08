package com.gstbilling.controler;


import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gstbilling.dao.UserRepository;
import com.gstbilling.dto.BusinessDetailsDto;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;
import com.gstbilling.service.BusinessDetailsService;
@RestController
@RequestMapping("/business")
public class BusinessDetailsController {

    @Autowired
    private BusinessDetailsService businessDetailsService;
    
    @Autowired 
    private UserRepository userRepository;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'USER')")
    public ResponseEntity<?> getMyBusinesses(
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName());
        if(user == null) 
        	throw new RuntimeException("Owner not found");
        
        if(user.getRole()==Role.ROLE_OWNER)
        	return ResponseEntity.ok(user.getBusinesses());
        	
        else {
        Set<BusinessDetails> businesses = user.getAssignedBusinesses();
       
        return ResponseEntity.ok(businesses);
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusinessDetails> getBusinessById(@PathVariable Long id) {
        try {
            BusinessDetails business = businessDetailsService.getBusinessDetailsById(id);
            return ResponseEntity.ok(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @GetMapping("/default")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusinessDetails> getDefaultBusiness() {
        try {
            BusinessDetails business = businessDetailsService.getDefaultBusinessDetails();
            return ResponseEntity.ok(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusinessDetails> createBusiness(

            @RequestPart("business")
            BusinessDetailsDto dto,

            @RequestPart(value = "logo", required = false)
            MultipartFile logo,

            Authentication authentication) {

        try {

            String username = authentication.getName();

            BusinessDetails created =
                    businessDetailsService.createBusinessDetails(
                            dto,
                            username,
                            logo
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusinessDetails> updateBusiness(

            @PathVariable Long id,

            @RequestPart("business")
            BusinessDetailsDto dto,

            @RequestPart(value = "logo", required = false)
            MultipartFile logo) {

        try {

            BusinessDetails updated =
                    businessDetailsService.updateBusinessDetails(
                            id,
                            dto,
                            logo
                    );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> deleteBusiness(@PathVariable Long id) {
        try {
            businessDetailsService.deleteBusinessDetails(id);
            return ResponseEntity.ok("Business deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Business not found with id: " + id);
        }
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        Boolean active = request.get("active");

        if (active == null) {
            return ResponseEntity.badRequest()
                    .body("Active status is required.");
        }

        businessDetailsService.updateStatus(id, active);

        return ResponseEntity.ok(
                active
                    ? "Business activated successfully."
                    : "Business deactivated successfully."
        );
    }
}