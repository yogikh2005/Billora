package com.gstbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentInvoiceDto {

    private Long id;

    private String invoiceNumber;

    private String customerName;

    private Double amount;

    private String status;
}
