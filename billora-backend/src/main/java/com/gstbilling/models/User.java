package com.gstbilling.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "users")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {


    // ==========================================
    // ID
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    // ==========================================
    // Basic Information
    // ==========================================

    private String username;

    private String email;

    private String password;


    // ==========================================
    // Role
    // ==========================================

    @Enumerated(EnumType.STRING)
    private Role role;


    // ==========================================
    // Active
    // ==========================================

    private boolean active;


    // ==========================================
    // Created At
    // ==========================================

    @CreationTimestamp
    private LocalDateTime createdAt;


    // ==========================================
    // Updated At
    // ==========================================

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ==========================================
    // Worker -> Owner
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User owner;


    // ==========================================
    // Owner -> Businesses
    // ==========================================

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<BusinessDetails> businesses =
            new ArrayList<>();


    // ==========================================
    // Worker -> Assigned Businesses
    // ==========================================

    @ManyToMany(mappedBy = "workers")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<BusinessDetails> assignedBusinesses =
            new HashSet<>();


	public User orElseThrow(Object object) {
		// TODO Auto-generated method stub
		return null;
	}


}