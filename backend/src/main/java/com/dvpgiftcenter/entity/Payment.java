package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENTS")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate = LocalDateTime.now();
    
    @NotNull
    @Size(max = 50)
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;
    
    @Size(max = 100)
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @NotNull
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    
    // Constructors
    public Payment() {}
    
    public Payment(Transaction transaction, String paymentMethod, BigDecimal amountPaid, String status) {
        this.transaction = transaction;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.status = status;
        this.paymentDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}