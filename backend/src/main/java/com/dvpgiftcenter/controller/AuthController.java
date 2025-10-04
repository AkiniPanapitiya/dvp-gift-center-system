package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.auth.JwtResponse;
import com.dvpgiftcenter.dto.auth.LoginRequest;
import com.dvpgiftcenter.dto.auth.RegisterRequest;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.entity.User;
import com.dvpgiftcenter.repository.UserRepository;
import com.dvpgiftcenter.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            User user = userRepository.findByUsernameAndIsActiveTrue(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String jwt = jwtUtils.generateToken(user.getUsername(), user.getRole());
            
            JwtResponse jwtResponse = new JwtResponse(
                jwt,
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
            
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Check if username exists
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is already taken"));
            }
            
            // Check if email exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is already in use"));
            }
            
            // Create new user
            User user = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(),
                registerRequest.getFullName(),
                "customer" // Default role for registration
            );
            
            user.setPhone(registerRequest.getPhone());
            user.setAddress(registerRequest.getAddress());
            
            userRepository.save(user);
            
            return ResponseEntity.ok(
                ApiResponse.success("User registered successfully")
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
}