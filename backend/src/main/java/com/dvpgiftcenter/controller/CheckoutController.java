package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.checkout.CheckoutRequest;
import com.dvpgiftcenter.dto.checkout.CheckoutResponse;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/online")
@CrossOrigin(origins = "*")
public class CheckoutController {
    
    @Autowired
    private CheckoutService checkoutService;
    
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            CheckoutResponse response = checkoutService.processCheckout(request, username);
            
            return ResponseEntity.ok(
                ApiResponse.success("Order placed successfully", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Checkout failed: " + e.getMessage()));
        }
    }
}