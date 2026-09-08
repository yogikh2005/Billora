package com.gstbilling.dto;

import java.util.List;

import com.gstbilling.models.Invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private DashboardStats stats;

    private List<RecentInvoiceDto> recentInvoices;

    private List<RecentUserDto> recentUsers;
}