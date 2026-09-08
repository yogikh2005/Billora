package com.gstbilling.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    // =========================
    // Business
    // =========================

    private Long businessId;


    // =========================
    // Customer
    // =========================

    private String customerName;

    private String customerEmail;

    private String customerAddress;

    private String customerGstNo;

    private String customerPhone;


    // =========================
    // Invoice
    // =========================

    private LocalDate invoiceDate;

    private String status;


    // =========================
    // Backward compatibility
    // =========================

    private Double amount;


    // =========================
    // Items
    // =========================

    private List<InvoiceItemDTO> items;
}