package com.gstbilling.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Invoice;
import com.gstbilling.models.User;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    List<Invoice> findByInvoiceDateBetween(
        LocalDate startDate,
        LocalDate endDate
    );
    
    
    Page<Invoice> findByBusinessIn(
            Set<BusinessDetails> businesses,
            Pageable pageable
    );
    List<Invoice>
    findByCustomerNameContainingIgnoreCase(
        String customerName
    );


    List<Invoice>
    findByInvoiceNumber(
        String invoiceNumber
    );


    List<Invoice>
    findByStatus(
        String status
    );


    // Owner invoices
    List<Invoice>
    findByOwner(User owner);


    // User-created invoices
    List<Invoice>
    findByCreatedBy(User user);
    
    
    @Query("""
    	    SELECT COALESCE(SUM(i.totalAmount), 0)
    	    FROM Invoice i
    	""")
    	BigDecimal getTotalRevenue();
    
    long countByBusinessIn(Collection<BusinessDetails> businesses);
    
    
    
    @Query("""
    	    SELECT COALESCE(SUM(i.totalAmount), 0)
    	    FROM Invoice i
    	    WHERE i.business IN :businesses
    	""")
    	BigDecimal getTotalRevenueByBusinessIn(
    	        @Param("businesses") Collection<BusinessDetails> businesses
    	);
   
    List<Invoice> findTop3ByBusinessInOrderByCreatedAtDesc(Collection<BusinessDetails> businesses);
    
    
    
 
		List<Invoice> findByBusinessIdAndInvoiceDateBetween(
		    Long businessId,
		    LocalDate startDate,
		    LocalDate endDate
		);

}