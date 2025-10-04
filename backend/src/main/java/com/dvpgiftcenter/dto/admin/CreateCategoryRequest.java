package com.dvpgiftcenter.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequest {
    
    @NotBlank
    @Size(max = 100)
    private String categoryName;
    
    private String description;
    
    private Boolean isActive = true;
    
    // Constructors
    public CreateCategoryRequest() {}
    
    public CreateCategoryRequest(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}