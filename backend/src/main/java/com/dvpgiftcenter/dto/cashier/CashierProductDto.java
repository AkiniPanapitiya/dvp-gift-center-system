package com.dvpgiftcenter.dto.cashier;

import java.math.BigDecimal;

public class CashierProductDto {
    private Long productId;
    private String productName;
    private String productCode;
    private String barcode;
    private String description;
    private BigDecimal unitPrice;
    private String categoryName;
    private String imageUrl;
    private Integer availableStock;
    private Boolean isActive;
    
    // Constructors
    public CashierProductDto() {}
    
    public CashierProductDto(Long productId, String productName, String productCode, 
                           String barcode, String description, BigDecimal unitPrice, 
                           String categoryName, String imageUrl, Integer availableStock, 
                           Boolean isActive) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.barcode = barcode;
        this.description = description;
        this.unitPrice = unitPrice;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.availableStock = availableStock;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Integer getAvailableStock() {
        return availableStock;
    }
    
    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}