package com.dvpgiftcenter.dto.product;

import java.math.BigDecimal;

public class ProductDto {
    
    private Long id; // Changed from productId to id
    private String name; // Changed from productName to name
    private String productCode;
    private String barcode;
    private String description;
    private BigDecimal price; // Changed from unitPrice to price
    private BigDecimal costPrice;
    private String imageUrl;
    private Boolean isActive;
    private String category; // Changed from categoryName to category
    private Integer categoryId;
    private Integer stockQuantity; // Changed from currentStock to stockQuantity
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Boolean isAvailableOnline; // Online availability flag
    private BigDecimal onlinePrice; // Online price
    private String onlineDescription; // Online description
    private String promotionalDetails; // Promotional details
    
    // Constructors
    public ProductDto() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
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
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    
    public Boolean getIsAvailableOnline() { return isAvailableOnline; }
    public void setIsAvailableOnline(Boolean isAvailableOnline) { this.isAvailableOnline = isAvailableOnline; }
    
    public BigDecimal getOnlinePrice() { return onlinePrice; }
    public void setOnlinePrice(BigDecimal onlinePrice) { this.onlinePrice = onlinePrice; }
    
    public String getOnlineDescription() { return onlineDescription; }
    public void setOnlineDescription(String onlineDescription) { this.onlineDescription = onlineDescription; }
    
    public String getPromotionalDetails() { return promotionalDetails; }
    public void setPromotionalDetails(String promotionalDetails) { this.promotionalDetails = promotionalDetails; }
}