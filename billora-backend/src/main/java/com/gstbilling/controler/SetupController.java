package com.gstbilling.controler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.dto.SetupDTO;
import com.gstbilling.service.SetupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;


    // =========================
    // Check Setup Status
    // =========================

    @GetMapping("/status")
    public ResponseEntity<String> status() {

        if (setupService.isSetupRequired()) {

            return ResponseEntity.ok("SETUP_REQUIRED");
        }

        return ResponseEntity.ok("SETUP_COMPLETED");
    }


    // =========================
    // Create Owner
    // =========================

    @PostMapping
    public ResponseEntity<?> setup(
            @RequestBody SetupDTO dto) {

        if (!setupService.isSetupRequired()) {

            return ResponseEntity
                    .status(409)
                    .body("Setup already completed.");
        }

        setupService.createOwner(dto);

        return ResponseEntity.ok(
                "Owner created successfully."
        );
    }
}