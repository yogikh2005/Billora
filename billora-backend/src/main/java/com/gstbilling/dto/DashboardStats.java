package com.gstbilling.dto;

import java.math.BigDecimal;
import java.util.List;

import com.gstbilling.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    private Long totalUsers;

    private Long totalBusinesses;

    private Long totalInvoices;

    private BigDecimal totalRevenue;
}