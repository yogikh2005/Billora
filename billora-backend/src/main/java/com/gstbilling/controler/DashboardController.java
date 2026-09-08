package com.gstbilling.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.dao.UserRepository;
import com.gstbilling.dto.DashboardResponse;
import com.gstbilling.models.User;
import com.gstbilling.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {

    	 User user = userRepository.findByUsername(authentication.getName());
            
        return ResponseEntity.ok( dashboardService.getDashboard(user));
    }
}