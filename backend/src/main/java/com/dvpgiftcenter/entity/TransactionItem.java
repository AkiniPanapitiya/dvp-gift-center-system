package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "TRANSACTION_ITEMS")
public class TransactionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Transaction transaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Product product;
    
    @NotNull
    @Min(1)
    @Column(name = "quantity")
    private Integer quantity;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "line_total", precision = 10, scale = 2)
    private BigDecimal lineTotal;
    
    @DecimalMin(value = "0.0")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Min(0)
    @Column(name = "return_quantity")
    private Integer returnQuantity = 0;
    
    // Constructors
    public TransactionItem() {}
    
    public TransactionItem(Transaction transaction, Product product, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.transaction = transaction;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
        this.returnQuantity = 0;
    }
    
    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public Integer getReturnQuantity() { return returnQuantity; }
    public void setReturnQuantity(Integer returnQuantity) { this.returnQuantity = returnQuantity; }
}