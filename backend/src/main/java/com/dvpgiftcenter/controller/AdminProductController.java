package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.admin.CreateProductRequest;
import com.dvpgiftcenter.dto.admin.UpdateProductRequest;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.dto.product.ProductDto;
import com.dvpgiftcenter.service.AdminProductService;
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
public class AdminProductController {
    
    @Autowired
    private AdminProductService adminProductService;
    
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts(
            @RequestParam(name = "availability", required = false) String availability,
            @RequestParam(name = "includeArchived", required = false, defaultValue = "false") boolean includeArchived) {
        try {
            // availability can be: null/empty (no filter), "online", or "offline"
            List<ProductDto> products = adminProductService.getAllProducts(availability, includeArchived);
            
            return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", products)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve products: " + e.getMessage()));
        }
    }
    
    @GetMapping("/products/active")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllActiveProducts() {
        try {
            List<ProductDto> products = adminProductService.getAllActiveProducts();
            
            return ResponseEntity.ok(
                ApiResponse.success("Active products retrieved successfully", products)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve active products: " + e.getMessage()));
        }
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        try {
            Optional<ProductDto> product = adminProductService.getProductById(id);
            
            if (product.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponse.success("Product retrieved successfully", product.get())
                );
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve product: " + e.getMessage()));
        }
    }
    
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        
        try {
            ProductDto product = adminProductService.createProduct(request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Product created successfully", product)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create product: " + e.getMessage()));
        }
    }
    
    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        try {
            ProductDto product = adminProductService.updateProduct(id, request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", product)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update product: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @PathVariable Long id,
            @RequestParam(name = "hard", required = false, defaultValue = "false") boolean hard) {
        try {
            if (hard) {
                adminProductService.deleteProduct(id, true);
            } else {
                adminProductService.deleteProduct(id);
            }
            
            return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully")
            );
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete product: " + e.getMessage()));
        }
    }

    @PutMapping("/products/{id}/restore")
    public ResponseEntity<ApiResponse<ProductDto>> restoreProduct(@PathVariable Long id) {
        try {
            ProductDto restored = adminProductService.restoreProduct(id);
            return ResponseEntity.ok(ApiResponse.success("Product restored successfully", restored));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to restore product: " + e.getMessage()));
        }
    }
}