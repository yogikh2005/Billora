package com.gstbilling.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    // =========================
    // Invoice Information
    // =========================

    private String invoiceNumber;

    private LocalDate invoiceDate;

    private String status;


    // =========================
    // Business
    // =========================

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private BusinessDetails business;


    // =========================
    // Owner
    // =========================

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User owner;


    // =========================
    // Created By
    // =========================

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User createdBy;


    // =========================
    // Customer
    // =========================

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Customer customer;


    // =========================
    // Invoice Items
    // =========================

    @OneToMany(
        mappedBy = "invoice",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private List<InvoiceItem> items = new ArrayList<>();


    // =========================
    // Amounts
    // =========================

    private Double subtotal;

    private Double gstAmount;

    private Double totalAmount;


    // =========================
    // Created
    // =========================

    @CreationTimestamp
    private LocalDateTime createdAt;


    // =========================
    // Add Item
    // =========================

    public void addItem(InvoiceItem item) {

        items.add(item);

        item.setInvoice(this);
    }


    // =========================
    // Remove Item
    // =========================

    public void removeItem(InvoiceItem item) {

        items.remove(item);

        item.setInvoice(null);
    }


    // =========================
    // Calculate Totals
    // =========================

    public void calculateTotals() {

        this.subtotal = items.stream()
            .mapToDouble(
                item ->
                    item.getQuantity()
                    * item.getPrice()
            )
            .sum();


        this.gstAmount = items.stream()
            .mapToDouble(
                InvoiceItem::getGstAmount
            )
            .sum();


        this.totalAmount =
            this.subtotal
            + this.gstAmount;
    }
}