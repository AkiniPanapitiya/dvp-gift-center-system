package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.admin.CreateCategoryRequest;
import com.dvpgiftcenter.dto.admin.UpdateCategoryRequest;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.entity.Category;
import com.dvpgiftcenter.repository.CategoryRepository;
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
public class AdminCategoryController {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            
            return ResponseEntity.ok(
                ApiResponse.success("Categories retrieved successfully", categories)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }
    
    @GetMapping("/categories/active")
    public ResponseEntity<ApiResponse<List<Category>>> getActiveCategories() {
        try {
            List<Category> categories = categoryRepository.findByIsActiveTrueOrderByCategoryName();
            
            return ResponseEntity.ok(
                ApiResponse.success("Active categories retrieved successfully", categories)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve active categories: " + e.getMessage()));
        }
    }
    
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        try {
            // Check if category name already exists
            if (categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Category name already exists: " + request.getCategoryName()));
            }
            
            Category category = new Category();
            category.setCategoryName(request.getCategoryName());
            category.setDescription(request.getDescription());
            category.setIsActive(request.getIsActive());
            
            Category savedCategory = categoryRepository.save(category);
            
            return ResponseEntity.ok(
                ApiResponse.success("Category created successfully", savedCategory)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create category: " + e.getMessage()));
        }
    }
    
    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Integer id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            
            if (category.isPresent()) {
                return ResponseEntity.ok(
                    ApiResponse.success("Category retrieved successfully", category.get())
                );
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve category: " + e.getMessage()));
        }
    }
    
    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Integer id, 
            @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            Optional<Category> existingCategory = categoryRepository.findById(id);
            
            if (!existingCategory.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Category category = existingCategory.get();
            
            // Check if new category name already exists (if changed)
            if (request.getCategoryName() != null && 
                !request.getCategoryName().equalsIgnoreCase(category.getCategoryName()) &&
                categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Category name already exists: " + request.getCategoryName()));
            }
            
            // Update fields if provided
            if (request.getCategoryName() != null) {
                category.setCategoryName(request.getCategoryName());
            }
            if (request.getDescription() != null) {
                category.setDescription(request.getDescription());
            }
            if (request.getIsActive() != null) {
                category.setIsActive(request.getIsActive());
            }
            
            Category updatedCategory = categoryRepository.save(category);
            
            return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully", updatedCategory)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update category: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Integer id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            
            if (!category.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Instead of hard delete, mark as inactive
            Category cat = category.get();
            cat.setIsActive(false);
            categoryRepository.save(cat);
            
            return ResponseEntity.ok(
                ApiResponse.success("Category deactivated successfully")
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete category: " + e.getMessage()));
        }
    }
}