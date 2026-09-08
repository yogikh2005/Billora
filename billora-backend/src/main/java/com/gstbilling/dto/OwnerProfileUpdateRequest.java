package com.gstbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerProfileUpdateRequest {

    private String username;
    private String email;
    private String password;
}