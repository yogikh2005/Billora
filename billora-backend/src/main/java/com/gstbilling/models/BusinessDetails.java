package com.gstbilling.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Table(name = "business_details")
public class BusinessDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

    @Column(nullable = false)
    private String businessName;

    @Column(length = 500)
    private String address;
    
    private String city;
    
    private String state;
    
    private String pincode;

    @Column(unique = true, nullable = false)
    private String gstin;

    private String email;

    private String mobileNo;
    
    private String phoneNo;
    
    private String website;

    private String logoPath;

    // Bank details
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branch;

    @Column(length = 1000)
    private String termsAndConditions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User owner;


    @ManyToMany
    @JoinTable(
        name = "business_workers",
        joinColumns = @JoinColumn(name = "business_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<User> workers = new HashSet<>();
   
    public void addWorker(User worker) {

        if (workers == null) {
            workers = new HashSet<>();
        }

        if (worker.getAssignedBusinesses() == null) {
            worker.setAssignedBusinesses(
                    new HashSet<>()
            );
        }

        workers.add(worker);

        worker.getAssignedBusinesses().add(this);
    }


    public void removeWorker(User worker) {

        workers.remove(worker);

        if (worker.getAssignedBusinesses() != null) {
            worker.getAssignedBusinesses().remove(this);
        }
    }
    
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    
}