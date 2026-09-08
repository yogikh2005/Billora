package com.gstbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {

    private DashboardStatsDto stats;

    private List<RecentInvoiceDto> recentInvoices;

    private List<RecentUserDto> recentUsers;
}