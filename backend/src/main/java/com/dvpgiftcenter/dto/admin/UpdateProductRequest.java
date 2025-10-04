package com.dvpgiftcenter.dto.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateProductRequest {
    
    @Size(max = 150)
    private String name; // Changed from productName to name
    
    private String category; // This will be category name
    
    @Size(max = 50)
    private String productCode;
    
    @Size(max = 100)
    private String barcode;
    
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price; // Changed from unitPrice to price
    
    @DecimalMin(value = "0.0")
    private BigDecimal costPrice;
    
    @Size(max = 255)
    private String imageUrl;
    
    private Boolean isActive;
    
    // Stock information
    private Integer stockQuantity; // Changed from currentStock to stockQuantity
    private Integer minStockLevel;
    private Integer maxStockLevel;
    
    // Online product information
    private Boolean isAvailableOnline;
    private BigDecimal onlinePrice;
    private String onlineDescription;
    private String promotionalDetails;
    
    // Constructors
    public UpdateProductRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    
    // Online product getters and setters
    public Boolean getIsAvailableOnline() { return isAvailableOnline; }
    public void setIsAvailableOnline(Boolean isAvailableOnline) { this.isAvailableOnline = isAvailableOnline; }
    
    public BigDecimal getOnlinePrice() { return onlinePrice; }
    public void setOnlinePrice(BigDecimal onlinePrice) { this.onlinePrice = onlinePrice; }
    
    public String getOnlineDescription() { return onlineDescription; }
    public void setOnlineDescription(String onlineDescription) { this.onlineDescription = onlineDescription; }
    
    public String getPromotionalDetails() { return promotionalDetails; }
    public void setPromotionalDetails(String promotionalDetails) { this.promotionalDetails = promotionalDetails; }
}