package com.dvpgiftcenter.dto.admin;

import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {
    
    @Size(max = 100)
    private String categoryName;
    
    private String description;
    
    private Boolean isActive;
    
    // Constructors
    public UpdateCategoryRequest() {}
    
    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}