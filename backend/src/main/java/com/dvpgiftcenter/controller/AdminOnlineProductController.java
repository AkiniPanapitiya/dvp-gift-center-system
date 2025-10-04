package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.admin.CreateOnlineProductRequest;
import com.dvpgiftcenter.dto.admin.UpdateOnlineProductRequest;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.dto.product.OnlineProductDto;
import com.dvpgiftcenter.service.AdminOnlineProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminOnlineProductController {
    
    @Autowired
    private AdminOnlineProductService adminOnlineProductService;
    
    @GetMapping("/online-products")
    public ResponseEntity<ApiResponse<List<OnlineProductDto>>> getAllOnlineProducts() {
        try {
            List<OnlineProductDto> products = adminOnlineProductService.getAllOnlineProducts();
            
            return ResponseEntity.ok(
                ApiResponse.success("Online products retrieved successfully", products)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve online products: " + e.getMessage()));
        }
    }
    
    @GetMapping("/online-products/{id}")
    public ResponseEntity<ApiResponse<OnlineProductDto>> getOnlineProductById(@PathVariable Long id) {
        try {
            Optional<OnlineProductDto> product = adminOnlineProductService.getOnlineProductById(id);
            
            if (product.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponse.success("Online product retrieved successfully", product.get())
                );
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve online product: " + e.getMessage()));
        }
    }
    
    @PostMapping("/online-products")
    public ResponseEntity<ApiResponse<OnlineProductDto>> createOnlineProduct(
            @Valid @RequestBody CreateOnlineProductRequest request) {
        
        try {
            OnlineProductDto product = adminOnlineProductService.createOnlineProduct(request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Online product created successfully", product)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create online product: " + e.getMessage()));
        }
    }
    
    @PutMapping("/online-products/{id}")
    public ResponseEntity<ApiResponse<OnlineProductDto>> updateOnlineProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOnlineProductRequest request) {
        
        try {
            OnlineProductDto product = adminOnlineProductService.updateOnlineProduct(id, request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Online product updated successfully", product)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update online product: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/online-products/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOnlineProduct(@PathVariable Long id) {
        try {
            adminOnlineProductService.deleteOnlineProduct(id);
            
            return ResponseEntity.ok(
                ApiResponse.success("Online product deleted successfully")
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete online product: " + e.getMessage()));
        }
    }
}