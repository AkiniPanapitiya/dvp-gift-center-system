package com.dvpgiftcenter.dto.checkout;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CheckoutResponse {
    
    private Long orderId;
    private Long transactionId;
    private String billNumber;
    private String referenceNumber;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private String orderStatus;
    private LocalDateTime placedAt;
    private String message;
    
    // Constructors
    public CheckoutResponse() {}
    
    public CheckoutResponse(Long orderId, Long transactionId, String billNumber, 
                          BigDecimal totalAmount, BigDecimal taxAmount, BigDecimal netAmount,
                          String orderStatus, LocalDateTime placedAt, String message) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.billNumber = billNumber;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.netAmount = netAmount;
        this.orderStatus = orderStatus;
        this.placedAt = placedAt;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    
    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}