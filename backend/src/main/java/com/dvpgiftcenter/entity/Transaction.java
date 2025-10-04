package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Staff/Cashier
    
    @NotNull
    @Size(max = 50)
    @Column(name = "bill_number", unique = true)
    private String billNumber;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate = LocalDateTime.now();
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @DecimalMin(value = "0.0")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;
    
    @NotNull
    @Size(max = 20)
    @Column(name = "transaction_type")
    private String transactionType;
    
    @NotNull
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    
    @Column(name = "receipt_printed")
    private Boolean receiptPrinted = false;
    
    @Column(name = "email_sent")
    private Boolean emailSent = false;
    
    @Size(max = 20)
    @Column(name = "source")
    private String source = "pos_sale";
    
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TransactionItem> transactionItems;
    
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    @OneToOne(mappedBy = "transaction", fetch = FetchType.LAZY)
    private OnlineOrder onlineOrder;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(User customer, User user, String billNumber, BigDecimal totalAmount, 
                      BigDecimal netAmount, String transactionType, String status, String source) {
        this.customer = customer;
        this.user = user;
        this.billNumber = billNumber;
        this.totalAmount = totalAmount;
        this.netAmount = netAmount;
        this.transactionType = transactionType;
        this.status = status;
        this.source = source;
        this.transactionDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getReceiptPrinted() { return receiptPrinted; }
    public void setReceiptPrinted(Boolean receiptPrinted) { this.receiptPrinted = receiptPrinted; }
    
    public Boolean getEmailSent() { return emailSent; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public List<TransactionItem> getTransactionItems() { return transactionItems; }
    public void setTransactionItems(List<TransactionItem> transactionItems) { this.transactionItems = transactionItems; }
    
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    
    public OnlineOrder getOnlineOrder() { return onlineOrder; }
    public void setOnlineOrder(OnlineOrder onlineOrder) { this.onlineOrder = onlineOrder; }
}