package com.dvpgiftcenter.dto.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateOnlineProductRequest {
    
    @NotNull
    private Long productId;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal onlinePrice;
    
    private String onlineDescription;
    
    private String promotionalDetails;
    
    private Boolean isAvailableOnline = true;
    
    // Constructors
    public CreateOnlineProductRequest() {}
    
    public CreateOnlineProductRequest(Long productId, BigDecimal onlinePrice) {
        this.productId = productId;
        this.onlinePrice = onlinePrice;
        this.isAvailableOnline = true;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public BigDecimal getOnlinePrice() { return onlinePrice; }
    public void setOnlinePrice(BigDecimal onlinePrice) { this.onlinePrice = onlinePrice; }
    
    public String getOnlineDescription() { return onlineDescription; }
    public void setOnlineDescription(String onlineDescription) { this.onlineDescription = onlineDescription; }
    
    public String getPromotionalDetails() { return promotionalDetails; }
    public void setPromotionalDetails(String promotionalDetails) { this.promotionalDetails = promotionalDetails; }
    
    public Boolean getIsAvailableOnline() { return isAvailableOnline; }
    public void setIsAvailableOnline(Boolean isAvailableOnline) { this.isAvailableOnline = isAvailableOnline; }
}