package com.gstbilling.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gstbilling.dao.UserRepository;
import com.gstbilling.dto.SetupDTO;
import com.gstbilling.models.Role;
import com.gstbilling.models.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isSetupRequired() {
    	System.out.println("Setup is reuied method");
        return userRepository.findByRole(Role.ROLE_OWNER).isEmpty();
    }

    public void createOwner(SetupDTO dto) {

        if (!isSetupRequired()) {
            throw new IllegalStateException("Setup has already been completed.");
        }

        User owner = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ROLE_OWNER)
                .active(true)
                .build();

        userRepository.save(owner);
    }
}