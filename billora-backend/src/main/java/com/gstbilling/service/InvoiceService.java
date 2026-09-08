package com.gstbilling.service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.gstbilling.dao.CustomerRepository;
import com.gstbilling.dao.InvoiceRepository;
import com.gstbilling.dao.UserRepository;
import com.gstbilling.dto.InvoiceItemDTO;
import com.gstbilling.dto.InvoiceRequest;
import com.gstbilling.dto.InvoiceResponse;
import com.gstbilling.helper.ReportGenerator;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Customer;
import com.gstbilling.models.Invoice;
import com.gstbilling.models.InvoiceItem;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private  ReportGenerator reportGenerator;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BusinessDetailsService businessDetailsService;
    
    @Autowired
    private UserRepository userRepository;
    
    
    public Page<InvoiceResponse> getAllInvoices(int page, int size) {
    	
    	Authentication authentication = SecurityContextHolder
    	        .getContext()
    	        .getAuthentication();
    	
        User user = userRepository.findByUsername(authentication.getName());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Set<BusinessDetails> businesses;

        if (user.getRole() == Role.ROLE_OWNER) {

            businesses = new HashSet<>(user.getBusinesses());

        } else {

            businesses = user.getAssignedBusinesses();
        }

        if (businesses == null || businesses.isEmpty()) {
            return Page.empty();
        }

        return getInvoicesForAssignedBusinesses(
                businesses,
                page,
                size
        );
    }
        
    
    
    private Page<InvoiceResponse> getInvoicesForAssignedBusinesses(
            Set<BusinessDetails> businesses,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Invoice> invoices =
                invoiceRepository.findByBusinessIn(
                        businesses,
                        pageable
                );

        invoices.getContent().forEach(invoice ->
                System.out.println(invoice.getInvoiceNumber())
        );
        return invoices.map(this::mapToInvoiceResponse);
    }

  
    
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }
    
    public InvoiceResponse getInvoiceById2(Long id)
    {
    	return convertToDTO(getInvoiceById(id));
    		
    }
    private User getCurrentUser() {

        Authentication authentication =
            SecurityContextHolder
                .getContext()
                .getAuthentication();


        if (authentication == null ||
            !authentication.isAuthenticated()) {

            throw new RuntimeException(
                "User is not authenticated"
            );
        }


        String username =
            authentication.getName();

        
        User user =  userRepository.findByUsername(username);
        
        if(user == null)
        	  throw new RuntimeException("Logged-in user not found: " + username);
        
        return user;
    }

    public Invoice createInvoice(
            InvoiceRequest request) {


        // ============================
        // Logged-in User
        // ============================

        User currentUser =
            getCurrentUser();


        // ============================
        // Determine Owner
        // ============================

        User owner;


        if (Role.ROLE_OWNER.equals(currentUser.getRole())) {
            owner = currentUser;
        } else {
            owner = currentUser.getOwner();
        }


        if (owner == null) {

            throw new RuntimeException(
                "Owner not found for current user"
            );

        }


        // ============================
        // Business
        // ============================

        if (request.getBusinessId() == null) {

            throw new RuntimeException(
                "Business is required"
            );

        }


        BusinessDetails business =
            businessDetailsService
                .getBusinessDetailsById(
                    request.getBusinessId()
                );
        
        if (!business.isActive()) {
            throw new IllegalStateException(
                "Cannot create invoice for an inactive business."
            );
        }

        // ============================
        // Validate Business Owner
        // ============================

        if (business.getOwner() == null ||
            !business.getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException(
                "You do not have access to this business"
            );

        }


        // ============================
        // Customer
        // ============================

        Customer customer =
            customerRepository
                .findByEmail(
                    request.getCustomerEmail()
                )
                .orElse(
                    Customer.builder()

                        .name(
                            request.getCustomerName()
                        )

                        .email(
                            request.getCustomerEmail()
                        )

                        .phone(
                            request.getCustomerPhone()
                        )

                        .address(
                            request.getCustomerAddress()
                        )

                        .gstin(
                            request.getCustomerGstNo()
                        )

                        .build()
                );


        customer.setName(
            request.getCustomerName()
        );

        customer.setEmail(
            request.getCustomerEmail()
        );

        customer.setPhone(
            request.getCustomerPhone()
        );

        customer.setAddress(
            request.getCustomerAddress()
        );

        customer.setGstin(
            request.getCustomerGstNo()
        );


        customer =
            customerRepository.save(customer);


        // ============================
        // Invoice Number
        // ============================

        long count =
            invoiceRepository.count() + 1;


        String invoiceNumber =
            String.format(
                "%06d",
                count
            );


        // ============================
        // Create Invoice
        // ============================

        Invoice invoice =
            Invoice.builder()

                .invoiceNumber(
                    invoiceNumber
                )

                .invoiceDate(
                    request.getInvoiceDate()
                )

                .business(
                    business
                )

                .customer(
                    customer
                )

                .owner(
                		owner
                )

                .createdBy(
                    currentUser
                )

                .status(
                    request.getStatus()
                )

                .build();


        // ============================
        // Invoice Items
        // ============================

        if (request.getItems() != null &&
            !request.getItems().isEmpty()) {


            request.getItems()
                .forEach(itemDto -> {


                    InvoiceItem item =
                        InvoiceItem.builder()

                            .itemName(
                                itemDto.getItemName()
                            )

                            .quantity(
                                itemDto.getQuantity()
                            )

                            .price(
                                itemDto.getPrice()
                            )

                            .HSN(
                                itemDto.getHsn()
                            )

                            .gstRate(
                                itemDto.getGstRate()
                            )

                            .build();


                    double itemSubtotal =
                        item.getQuantity()
                        * item.getPrice();


                    double gstRate =
                        item.getGstRate() == null
                            ? 0
                            : item.getGstRate();


                    double gst =
                        itemSubtotal
                        * gstRate
                        / 100;


                    item.setGstAmount(gst);


                    item.setTotalWithGst(
                        itemSubtotal + gst
                    );


                    invoice.addItem(item);

                });


            invoice.calculateTotals();


        } else {

            double amount =
                request.getAmount() == null
                    ? 0
                    : request.getAmount();


            double gst =
                amount * 0.18;


            invoice.setSubtotal(
                amount
            );

            invoice.setGstAmount(
                gst
            );

            invoice.setTotalAmount(
                amount + gst
            );
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);

        try {

            sendInvoiceEmail(savedInvoice.getId());

        } catch (Exception e) {

            throw new RuntimeException(
                "Invoice created, but email sending failed: "
                + e.getMessage(),
                e
            );
        }

        return savedInvoice;
    }

    public Invoice updateInvoice(Long id, InvoiceRequest request) {
        Invoice existing = getInvoiceById(id);

        // Update business details if businessId is provided in request
        if (request.getBusinessId() != null) {
            BusinessDetails business = businessDetailsService.getBusinessDetailsById(request.getBusinessId());
            existing.setBusiness(business);
        }

        // Update customer
        Customer customer = existing.getCustomer();
        customer.setName(request.getCustomerName());
        customer.setEmail(request.getCustomerEmail());
        customer.setPhone(request.getCustomerPhone());
        customer.setAddress(request.getCustomerAddress());
        customer.setGstin(request.getCustomerGstNo());
        customerRepository.save(customer);

        // Update invoice basic info
        existing.setInvoiceDate(request.getInvoiceDate());
        existing.setStatus(request.getStatus());

        // Update items if present
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Clear existing items
            existing.getItems().clear();
            
            // Add new items
            request.getItems().forEach(itemDto -> {
                InvoiceItem item = InvoiceItem.builder()
                    .itemName(itemDto.getItemName())
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .HSN(itemDto.getHsn())
                    .gstRate(itemDto.getGstRate())
                    .build();
                
                // Calculate item totals
                double itemSubtotal = item.getQuantity() * item.getPrice();
                item.setGstAmount(itemSubtotal * item.getGstRate() / 100);
                item.setTotalWithGst(itemSubtotal + item.getGstAmount());
                
                existing.addItem(item);
            });
            
            // Recalculate invoice totals
            existing.calculateTotals();
        } else {
            // Fallback: use amount from request (backward compatibility)
            double gst = request.getAmount() * 0.18;
            existing.setSubtotal(request.getAmount());
            existing.setGstAmount(gst);
            existing.setTotalAmount(request.getAmount() + gst);
        }

        Invoice savedInvoice = invoiceRepository.save(existing);

        try {

            sendInvoiceEmail(savedInvoice.getId());

        } catch (Exception e) {

            throw new RuntimeException(
                "Invoice created, but email sending failed: "
                + e.getMessage(),
                e
            );
        }

        return savedInvoice;
    }

    public void deleteInvoice(Long id) {
        Invoice existing = getInvoiceById(id);
        invoiceRepository.delete(existing);
    }

   public void sendInvoiceEmail(Long id) {
        // Fetch the full invoice object
        Invoice invoice = getInvoiceById(id);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found for ID: " + id);
        }

        // --- CORE CHANGE IS HERE ---
        // Step 2: Use the ReportGenerator to create the actual PDF byte array
        byte[] pdfAttachment = reportGenerator.generatePdf(invoice);

        // Step 3: Check if the PDF was created successfully
        if (pdfAttachment == null) {
            throw new RuntimeException("Failed to generate PDF for invoice: " + invoice.getInvoiceNumber());
        }

        // Step 4: Send the email with the correctly generated PDF attachment
        emailService.sendInvoice(
        	    invoice,
        	    pdfAttachment
        	);
   }


    
    public byte[] generateReport(String type, Long businessId) {

        if (businessId == null) {
            throw new IllegalArgumentException(
                "Business ID is required"
            );
        }

        LocalDate now = LocalDate.now();

        LocalDate startDate;


        switch (type.toLowerCase()) {

            case "monthly":

                startDate =
                    now.withDayOfMonth(1);

                break;


            case "quarterly":

                int currentQuarter =
                    (now.getMonthValue() - 1) / 3 + 1;

                startDate =
                    LocalDate.of(
                        now.getYear(),
                        (currentQuarter - 1) * 3 + 1,
                        1
                    );

                break;


            case "annually":

                startDate =
                    LocalDate.of(
                        now.getYear(),
                        1,
                        1
                    );

                break;


            default:

                throw new IllegalArgumentException(
                    "Invalid report type: " + type
                );
        }


        // =========================================
        // BUSINESS + DATE FILTER
        // =========================================

        List<Invoice> invoices =
            invoiceRepository
                .findByBusinessIdAndInvoiceDateBetween(
                    businessId,
                    startDate,
                    now
                );


        return reportGenerator.generatePdf(
            invoices,
            type
        );
    }
    public byte[] generateCustomReport(
            String startDateStr,
            String endDateStr,
            Long businessId) {

        if (businessId == null) {
            throw new IllegalArgumentException(
                "Business ID is required"
            );
        }


        // =========================================
        // CLEAN INPUT
        // =========================================

        startDateStr =
            startDateStr.trim();

        endDateStr =
            endDateStr.trim();


        // =========================================
        // DATE FORMAT
        // =========================================

        DateTimeFormatter formatter =
            new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(
                    "[d-M-yyyy][dd-MM-yyyy][yyyy-MM-dd]"
                )
                .toFormatter();


        LocalDate startDate =
            LocalDate.parse(
                startDateStr,
                formatter
            );

        LocalDate endDate =
            LocalDate.parse(
                endDateStr,
                formatter
            );


        // =========================================
        // VALIDATE DATE
        // =========================================

        if (startDate.isAfter(endDate)) {

            throw new IllegalArgumentException(
                "Start date cannot be greater than end date"
            );
        }


        // =========================================
        // BUSINESS + DATE FILTER
        // =========================================

        List<Invoice> invoices =
            invoiceRepository
                .findByBusinessIdAndInvoiceDateBetween(
                    businessId,
                    startDate,
                    endDate
                );


        // =========================================
        // REPORT TYPE
        // =========================================

        String type =
            "custom_"
            + startDate
            + "_to_"
            + endDate;


        return reportGenerator.generatePdf(
            invoices,
            type
        );
    }
    
    
    private InvoiceResponse convertToDTO(Invoice invoice) {

    	InvoiceResponse dto = new InvoiceResponse();

        dto.setId(invoice.getId());

        dto.setInvoiceNumber(
                invoice.getInvoiceNumber()
        );

        dto.setInvoiceDate(
                invoice.getInvoiceDate()
        );

        dto.setStatus(
                invoice.getStatus()
        );

        dto.setSubtotal(
                invoice.getSubtotal()
        );

        dto.setGstAmount(
                invoice.getGstAmount()
        );

        dto.setTotalAmount(
                invoice.getTotalAmount()
        );

        dto.setCreatedAt(
                invoice.getCreatedAt()
        );


        // =========================
        // Business
        // =========================

        if (invoice.getBusiness() != null) {

            dto.setBusinessId(
                    invoice.getBusiness().getId()
            );

            dto.setBusinessName(
                    invoice.getBusiness().getBusinessName()
            );

            dto.setGstin(
                    invoice.getBusiness().getGstin()
            );
        }


        // =========================
        // Owner
        // =========================

        if (invoice.getOwner() != null) {

            dto.setOwnerId(
                    invoice.getOwner().getId()
            );

            dto.setOwnerUsername(
                    invoice.getOwner().getUsername()
            );
        }


        // =========================
        // Created By
        // =========================

        if (invoice.getCreatedBy() != null) {

            dto.setCreatedById(
                    invoice.getCreatedBy().getId()
            );

            dto.setCreatedByUsername(
                    invoice.getCreatedBy().getUsername()
            );
        }


        // =========================
        // Customer
        // =========================

        if (invoice.getCustomer() != null) {

            dto.setCustomerId(
                    invoice.getCustomer().getId()
            );

            dto.setCustomerName(
                    invoice.getCustomer().getName()
            );
            
            dto.setCustomerAddress(invoice.getCustomer().getAddress());
            
            dto.setCustomerEmail(invoice.getCustomer().getEmail());
            dto.setCustomerGstin(invoice.getCustomer().getGstin());
            dto.setCustomerPhone(invoice.getCustomer().getPhone());
        }


        // =========================
        // Items
        // =========================

        if (invoice.getItems() != null) {

            dto.setItems(invoice.getItems()
            );
        }

        return dto;
    }
    
    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .businessName(invoice.getBusiness().getBusinessName())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomer().getName())
                .totalAmount(invoice.getTotalAmount())
                .items(invoice.getItems())
                .status(invoice.getStatus())
                .invoiceDate(invoice.getInvoiceDate())
                .build();
    }
    
    
    
    public byte[] generateInvoicePdf(Long id) {

        Invoice invoice = getInvoiceById(id);

        if (invoice == null) {
            throw new RuntimeException(
                    "Invoice not found for ID: " + id
            );
        }

        byte[] pdfAttachment =
                reportGenerator.generatePdf(invoice);

        if (pdfAttachment == null) {
            throw new RuntimeException(
                    "Failed to generate PDF for invoice: "
                    + invoice.getInvoiceNumber()
            );
        }

        return pdfAttachment;
    }
    
    
    
    
    
    
    
    
    
    
    
    

}