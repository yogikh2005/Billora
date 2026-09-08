package com.gstbilling.service;

import java.awt.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.gstbilling.dao.InvoiceRepository;
import com.gstbilling.dao.UserRepository;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Invoice;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;
import com.gstbilling.dto.DashboardResponse;
import com.gstbilling.dto.DashboardStats;
import com.gstbilling.dto.RecentInvoiceDto;
import com.gstbilling.dto.RecentUserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final BusinessDetailsService businessDetailsService;
    private final InvoiceRepository invoiceRepository;
    
    public DashboardResponse getDashboard(User user) {
    
        if (user.getRole() == Role.ROLE_OWNER) {
            return getOwnerDashboard(user);
        }

        return getUserDashboard(user);
    }

    private DashboardResponse getOwnerDashboard(User user) {
    
        DashboardStats stats = DashboardStats.builder()
                .totalUsers(userRepository.count()-1)
                .totalBusinesses(businessDetailsService.countBusinesses())
                .totalInvoices(invoiceRepository.count())
                .totalRevenue(invoiceRepository.getTotalRevenue())
                .build();

        return DashboardResponse.builder()
                .stats(stats)
                .recentInvoices(
                        invoiceRepository
                        .findTop3ByBusinessInOrderByCreatedAtDesc(user.getBusinesses())
                        .stream()
                        .map(this::mapToRecentInvoiceDto)
                        .toList()
        )
             // Recent 5 users created by/belonging to this owner
                .recentUsers(
                	    userRepository
                	        .findTop5ByOwnerIdOrderByCreatedAtDesc(user.getId())
                	        .stream()
                	        .map(this::mapToRecentUserDto)
                	        .toList()
                	)
                
                .build();
    }

    private DashboardResponse getUserDashboard(User user ) {

    	Set<BusinessDetails> businesses = user.getAssignedBusinesses();
        DashboardStats stats = DashboardStats.builder()
                .totalUsers(null)
                .totalBusinesses(null)
                .totalInvoices(
                        invoiceRepository.countByBusinessIn(businesses)
                )
                .totalRevenue(
                        invoiceRepository.getTotalRevenueByBusinessIn(businesses)
                )
                .build();

        return DashboardResponse.builder()
                .stats(stats)
                .recentInvoices(
                        invoiceRepository
                                .findTop3ByBusinessInOrderByCreatedAtDesc(businesses)
                                .stream()
                                .map(this::mapToRecentInvoiceDto)
                                .toList()
                )
                .recentUsers(null)
                .build();
    }

    
    private RecentInvoiceDto mapToRecentInvoiceDto(Invoice invoice) {

        return RecentInvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomer().getName())
                .amount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .build();
    }
    
    private RecentUserDto mapToRecentUserDto(User user)
    {
    	return RecentUserDto.builder()
    			.id(user.getId())
    			.name(user.getUsername())
    			.email(user.getEmail())
    			.build();
    }
	
	
}