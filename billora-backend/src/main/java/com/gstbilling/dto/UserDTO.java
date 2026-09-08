package com.gstbilling.dto;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    
    private Set<BusinessSummaryDTO> businesses;
		
	}
    


