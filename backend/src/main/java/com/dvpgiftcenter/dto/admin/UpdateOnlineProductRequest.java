package com.dvpgiftcenter.dto.admin;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class UpdateOnlineProductRequest {
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal onlinePrice;
    
    private String onlineDescription;
    
    private String promotionalDetails;
    
    private Boolean isAvailableOnline;
    
    // Constructors
    public UpdateOnlineProductRequest() {}
    
    // Getters and Setters
    public BigDecimal getOnlinePrice() { return onlinePrice; }
    public void setOnlinePrice(BigDecimal onlinePrice) { this.onlinePrice = onlinePrice; }
    
    public String getOnlineDescription() { return onlineDescription; }
    public void setOnlineDescription(String onlineDescription) { this.onlineDescription = onlineDescription; }
    
    public String getPromotionalDetails() { return promotionalDetails; }
    public void setPromotionalDetails(String promotionalDetails) { this.promotionalDetails = promotionalDetails; }
    
    public Boolean getIsAvailableOnline() { return isAvailableOnline; }
    public void setIsAvailableOnline(Boolean isAvailableOnline) { this.isAvailableOnline = isAvailableOnline; }
}