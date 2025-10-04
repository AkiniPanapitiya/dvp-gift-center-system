package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.dto.product.OnlineProductDto;
import com.dvpgiftcenter.service.OnlineProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/online")
@CrossOrigin(origins = "*")
public class OnlineProductController {
    
    @Autowired
    private OnlineProductService onlineProductService;
    
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<OnlineProductDto>>> getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        try {
            List<OnlineProductDto> products;
            
            if (categoryId != null || search != null || minPrice != null || maxPrice != null) {
                products = onlineProductService.searchProducts(categoryId, search, minPrice, maxPrice);
            } else {
                products = onlineProductService.getAllAvailableProducts();
            }
            
            return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", products)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve products: " + e.getMessage()));
        }
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<OnlineProductDto>> getProductById(@PathVariable Long id) {
        try {
            Optional<OnlineProductDto> product = onlineProductService.getProductById(id);
            
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
    
}