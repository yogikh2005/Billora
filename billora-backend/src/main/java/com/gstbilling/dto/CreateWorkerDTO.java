package com.gstbilling.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class CreateWorkerDTO {

    private String username;

    private String email;

    private String password;
    
    private Boolean active;
    
    private Set<Long> businessIds = new HashSet<>();
}