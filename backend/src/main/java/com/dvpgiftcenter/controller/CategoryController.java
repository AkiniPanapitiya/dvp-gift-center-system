package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.entity.Category;
import com.dvpgiftcenter.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/online")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findByIsActiveTrueOrderByCategoryName();
            
            return ResponseEntity.ok(
                ApiResponse.success("Categories retrieved successfully", categories)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }
    
    // Product endpoints are handled by OnlineProductController to avoid duplicate mappings
}