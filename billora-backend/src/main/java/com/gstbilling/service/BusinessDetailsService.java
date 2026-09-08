package com.gstbilling.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gstbilling.dao.BusinessDetailsRepository;
import com.gstbilling.dao.UserRepository;
import com.gstbilling.dto.BusinessDetailsDto;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
@Transactional
public class BusinessDetailsService {

    @Autowired
    private BusinessDetailsRepository businessDetailsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    
    
    
    public long countBusinesses() {
        return businessDetailsRepository.count();
    }
    
    public BusinessDetails createBusinessDetails(
        BusinessDetailsDto dto,
        String username,
        MultipartFile logo) {

    User owner =
            userRepository.findByUsername(username);

    if (owner == null) {
        throw new RuntimeException("Owner not found");
    }


    // =========================================
    // CHECK OWNER
    // =========================================

    if (owner.getRole() != Role.ROLE_OWNER) {

        throw new RuntimeException(
                "Only owner can create a business");
    }


    // =========================================
    // GSTIN UNIQUE CHECK
    // =========================================

    if (businessDetailsRepository
            .existsByGstin(dto.getGstin())) {

        throw new RuntimeException(
                "Business with GSTIN "
                + dto.getGstin()
                + " already exists");
    }


    // =========================================
    // CREATE BUSINESS
    // =========================================

    BusinessDetails business =
            BusinessDetails.builder()

                .businessName(dto.getBusinessName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .gstin(dto.getGstin())
                .email(dto.getEmail())
                .mobileNo(dto.getMobileNo())
                .phoneNo(dto.getPhoneNo())
                .website(dto.getWebsite())

                // Don't take logoPath from DTO
                .logoPath(null)

                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .ifscCode(dto.getIfscCode())
                .active(true)
                .branch(dto.getBranch())
                .termsAndConditions(
                        dto.getTermsAndConditions()
                )

                .owner(owner)

                .build();


    // =========================================
    // SAVE BUSINESS FIRST
    // =========================================

    BusinessDetails savedBusiness =
            businessDetailsRepository.save(business);


    // =========================================
    // SAVE LOGO
    // =========================================

    if (logo != null && !logo.isEmpty()) {

        try {

            String uploadDirectory =
                    "uploads/logos";

            Path directory =
                    Paths.get(uploadDirectory);

            Files.createDirectories(directory);


            // -------------------------------------
            // GET FILE EXTENSION
            // -------------------------------------

            String originalFileName =
                    logo.getOriginalFilename();

            String extension = "";

            if (originalFileName != null
                    && originalFileName.contains(".")) {

                extension =
                        originalFileName.substring(
                                originalFileName
                                        .lastIndexOf(".")
                        ).toLowerCase();
            }


            // -------------------------------------
            // VALIDATE EXTENSION
            // -------------------------------------

            if (!extension.equals(".png")
                    && !extension.equals(".jpg")
                    && !extension.equals(".jpeg")) {

                throw new RuntimeException(
                        "Only PNG, JPG and JPEG logos are allowed"
                );
            }


            // -------------------------------------
            // CREATE OUR OWN FILE NAME
            // -------------------------------------

            String fileName =
                    "business_"
                    + savedBusiness.getId()
                    + extension;


            Path filePath =
                    directory.resolve(fileName);


            // -------------------------------------
            // SAVE FILE
            // -------------------------------------

            Files.copy(
                    logo.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // -------------------------------------
            // SAVE PATH IN DATABASE
            // -------------------------------------

            savedBusiness.setLogoPath(
                    filePath.toString()
            );


            savedBusiness =
                    businessDetailsRepository.save(
                            savedBusiness
                    );


        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save business logo",
                    e
            );
        }
    }


    return savedBusiness;
}

   
    public List<BusinessDetails> getAllBusinessDetails() {
        return businessDetailsRepository.findAll();
    }

    public BusinessDetails getBusinessDetailsById(Long id) {
        return businessDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found with id: " + id));
    }
    
    public BusinessDetails getBusinessDetailsByName(String name)
    {
    		return businessDetailsRepository.findByBusinessName(name)
    				.orElseThrow(()-> new RuntimeException("Business not found with name: " + name));
    }

    public BusinessDetails getBusinessByGstin(String gstin) {
        return businessDetailsRepository.findByGstin(gstin)
                .orElseThrow(() -> new RuntimeException("Business not found with GSTIN: " + gstin));
    }

    public BusinessDetails getDefaultBusinessDetails() {
        List<BusinessDetails> businesses = businessDetailsRepository.findAll();
        if (businesses.isEmpty()) {
            throw new RuntimeException("No business details found. Please create business details first.");
        }
        return businesses.get(0);
    }

 
 public BusinessDetails updateBusinessDetails(
        Long id,
        BusinessDetailsDto dto,
        MultipartFile logo) {

    BusinessDetails existing =
            getBusinessDetailsById(id);


    // =========================================
    // GSTIN UNIQUENESS
    // =========================================

    if (!existing.getGstin().equals(dto.getGstin())) {

        if (businessDetailsRepository
                .existsByGstin(dto.getGstin())) {

            throw new RuntimeException(
                    "Business with GSTIN "
                    + dto.getGstin()
                    + " already exists"
            );
        }
    }


    // =========================================
    // UPDATE BUSINESS DETAILS
    // =========================================

    existing.setBusinessName(
            dto.getBusinessName()
    );

    existing.setAddress(
            dto.getAddress()
    );

    existing.setCity(
            dto.getCity()
    );

    existing.setState(
            dto.getState()
    );

    existing.setPincode(
            dto.getPincode()
    );

    existing.setGstin(
            dto.getGstin()
    );

    existing.setEmail(
            dto.getEmail()
    );

    existing.setMobileNo(
            dto.getMobileNo()
    );

    existing.setPhoneNo(
            dto.getPhoneNo()
    );

    existing.setWebsite(
            dto.getWebsite()
    );


    existing.setBankName(
            dto.getBankName()
    );

    existing.setAccountNumber(
            dto.getAccountNumber()
    );

    existing.setIfscCode(
            dto.getIfscCode()
    );

    existing.setBranch(
            dto.getBranch()
    );

    existing.setActive(
            dto.isActive()
    );

    existing.setTermsAndConditions(
            dto.getTermsAndConditions()
    );


    // =========================================
    // SAVE BUSINESS FIRST
    // =========================================

    BusinessDetails saved =
            businessDetailsRepository.save(
                    existing
            );


    // =========================================
    // UPDATE LOGO ONLY IF NEW LOGO SELECTED
    // =========================================

    if (logo != null && !logo.isEmpty()) {

        try {

            String uploadDirectory =
                    "uploads/logos";


            Path directory =
                    Paths.get(uploadDirectory);


            Files.createDirectories(
                    directory
            );


            // =====================================
            // GET EXTENSION
            // =====================================

            String originalFileName =
                    logo.getOriginalFilename();


            String extension = "";


            if (originalFileName != null
                    && originalFileName.contains(".")) {

                extension =
                        originalFileName
                                .substring(
                                        originalFileName
                                                .lastIndexOf(".")
                                )
                                .toLowerCase();
            }


            // =====================================
            // VALIDATE EXTENSION
            // =====================================

            if (!extension.equals(".png")
                    && !extension.equals(".jpg")
                    && !extension.equals(".jpeg")) {

                throw new RuntimeException(
                        "Only PNG, JPG and JPEG logos are allowed"
                );
            }


            // =====================================
            // FILE NAME
            // =====================================

            String fileName =
                    "business_"
                    + saved.getId()
                    + extension;


            Path filePath =
                    directory.resolve(
                            fileName
                    );


            // =====================================
            // SAVE NEW LOGO
            // =====================================

            Files.copy(

                    logo.getInputStream(),

                    filePath,

                    StandardCopyOption
                            .REPLACE_EXISTING
            );


            // =====================================
            // UPDATE DATABASE PATH
            // =====================================

            saved.setLogoPath(
                    filePath.toString()
            );


            saved =
                    businessDetailsRepository.save(
                            saved
                    );


        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to update business logo",
                    e
            );
        }
    }


    return saved;
}
    public void deleteBusinessDetails(Long id) {
        BusinessDetails existing = getBusinessDetailsById(id);
        existing.setActive(false);
        businessDetailsRepository.delete(existing);
    }

    
    public boolean existsById(Long id) {
        return businessDetailsRepository.existsById(id);
    }

    public boolean existsByGstin(String gstin) {
        return businessDetailsRepository.existsByGstin(gstin);
    }
    
    
    public void updateStatus(Long id, boolean active) {

        BusinessDetails business =
        		businessDetailsRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Business not found"
                                ));

        business.setActive(active);

        businessDetailsRepository.save(business);
    }
    
//    public List<BusinessDetails> getMyBusinessDetails(String username) {
//
//        User user = userRepository.findByUsername(username);
//        if(owner == null) {
//                       throw new RuntimeException("Owner not found");}
//        List<BusinessDetails> business = user.getBusinesses();
//        return businessDetailsRepository.findByOwner(owner);
//    }
}