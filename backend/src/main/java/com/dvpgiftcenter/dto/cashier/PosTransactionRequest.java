package com.dvpgiftcenter.dto.cashier;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public class PosTransactionRequest {
    
    // Optional: when null, treated as Walk-in Customer (no DB record)
    private Long customerId;
    
    @NotNull(message = "Transaction items are required")
    @Valid
    private List<PosTransactionItem> items;
    
    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, CREDIT_CARD, DEBIT_CARD
    
    private String notes;
    
    // Constructors
    public PosTransactionRequest() {}
    
    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public List<PosTransactionItem> getItems() {
        return items;
    }
    
    public void setItems(List<PosTransactionItem> items) {
        this.items = items;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Nested class for transaction items
    public static class PosTransactionItem {
        
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
        
        @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be positive")
        private BigDecimal unitPrice;
        
        @DecimalMin(value = "0.0", message = "Discount must be non-negative")
        private BigDecimal discountAmount = BigDecimal.ZERO;
        
        // Constructors
        public PosTransactionItem() {}
        
        public PosTransactionItem(Long productId, Integer quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
        
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
        
        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }
        
        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }
    }
}