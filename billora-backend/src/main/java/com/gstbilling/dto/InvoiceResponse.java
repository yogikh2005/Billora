package com.gstbilling.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.gstbilling.models.InvoiceItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {

    private Long id;

    private String invoiceNumber;

    private LocalDate invoiceDate;

    private String status;

    private Double subtotal;

    private Double gstAmount;

    private Double totalAmount;

    private LocalDateTime createdAt;


    // Business
    private Long businessId;
    private String businessName;
    private String gstin;


    // Owner
    private Long ownerId;
    private String ownerUsername;


    // Created By
    private Long createdById;
    private String createdByUsername;


    // Customer
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private String customerGstin; 

    // Items
    private List<InvoiceItem> items;
}