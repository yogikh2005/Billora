package com.gstbilling.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.config.JWTUtil;
import com.gstbilling.dto.SignInDTO;
import com.gstbilling.dto.SignInResponseDTO;
import com.gstbilling.dto.UserDTO;
import com.gstbilling.models.User;
import com.gstbilling.service.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    
    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> generateToken(@RequestBody SignInDTO signInDTO) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getUsername(), signInDTO.getPassword()));
        String token = jwtUtil.generateToken(signInDTO.getUsername());

        User user = authService.findByUsername(signInDTO.getUsername());
        
        if (user==null) {
        	throw new RuntimeException("Invalid username or password");
        }
        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }
        UserDTO userDTO = mapToDTO(user);

        SignInResponseDTO response = new SignInResponseDTO(
            token,
            userDTO
        );

        return ResponseEntity.ok(response);
    }
    
    private UserDTO mapToDTO(User user) {
    	UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
    
    Thread thread;
    
}