package com.dvpgiftcenter.dto.product;

import java.math.BigDecimal;

public class OnlineProductDto {
    
    private Long productId;
    private String productName;
    private String description;
    private String onlineDescription;
    private BigDecimal onlinePrice;
    private String imageUrl;
    private String categoryName;
    private Integer categoryId;
    private Integer currentStock;
    private String promotionalDetails;
    // Quantity field is used when this DTO is returned as part of cart items
    private Integer quantity;
    
    // Constructors
    public OnlineProductDto() {}
    
    public OnlineProductDto(Long productId, String productName, String description, 
                           String onlineDescription, BigDecimal onlinePrice, String imageUrl,
                           String categoryName, Integer categoryId, Integer currentStock) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.onlineDescription = onlineDescription;
        this.onlinePrice = onlinePrice;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.currentStock = currentStock;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getOnlineDescription() { return onlineDescription; }
    public void setOnlineDescription(String onlineDescription) { this.onlineDescription = onlineDescription; }
    
    public BigDecimal getOnlinePrice() { return onlinePrice; }
    public void setOnlinePrice(BigDecimal onlinePrice) { this.onlinePrice = onlinePrice; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public String getPromotionalDetails() { return promotionalDetails; }
    public void setPromotionalDetails(String promotionalDetails) { this.promotionalDetails = promotionalDetails; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}