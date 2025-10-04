package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ONLINE_PRODUCTS")
public class OnlineProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "online_product_id")
    private Long onlineProductId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private Product product;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "online_price", precision = 10, scale = 2)
    private BigDecimal onlinePrice;
    
    @Column(name = "is_available_online")
    private Boolean isAvailableOnline = true;
    
    @Column(name = "online_description", columnDefinition = "TEXT")
    private String onlineDescription;
    
    @Column(name = "promotional_details", columnDefinition = "TEXT")
    private String promotionalDetails;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public OnlineProduct() {}
    
    public OnlineProduct(Product product, BigDecimal onlinePrice) {
        this.product = product;
        this.onlinePrice = onlinePrice;
        this.isAvailableOnline = true;
    }
    
    // Getters and Setters
    public Long getOnlineProductId() { return onlineProductId; }
    public void setOnlineProductId(Long onlineProductId) { this.onlineProductId = onlineProductId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public BigDecimal getOnlinePrice() { return onlinePrice; }
    public void setOnlinePrice(BigDecimal onlinePrice) { this.onlinePrice = onlinePrice; }
    
    public Boolean getIsAvailableOnline() { return isAvailableOnline; }
    public void setIsAvailableOnline(Boolean isAvailableOnline) { this.isAvailableOnline = isAvailableOnline; }
    
    public String getOnlineDescription() { return onlineDescription; }
    public void setOnlineDescription(String onlineDescription) { this.onlineDescription = onlineDescription; }
    
    public String getPromotionalDetails() { return promotionalDetails; }
    public void setPromotionalDetails(String promotionalDetails) { this.promotionalDetails = promotionalDetails; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}