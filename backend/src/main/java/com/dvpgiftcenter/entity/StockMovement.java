package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_MOVEMENTS")
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    @NotNull
    @Size(max = 50)
    @Column(name = "movement_type")
    private String movementType;
    
    @NotNull
    @Column(name = "quantity_change")
    private Integer quantityChange;
    
    @NotNull
    @Column(name = "previous_stock")
    private Integer previousStock;
    
    @NotNull
    @Column(name = "new_stock")
    private Integer newStock;
    
    @Column(name = "movement_date")
    private LocalDateTime movementDate = LocalDateTime.now();
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Constructors
    public StockMovement() {}
    
    public StockMovement(Product product, Transaction transaction, String movementType, 
                        Integer quantityChange, Integer previousStock, Integer newStock) {
        this.product = product;
        this.transaction = transaction;
        this.movementType = movementType;
        this.quantityChange = quantityChange;
        this.previousStock = previousStock;
        this.newStock = newStock;
    }
    
    public StockMovement(Product product, Transaction transaction, String movementType, 
                        Integer quantityChange, Integer previousStock, Integer newStock, String notes) {
        this(product, transaction, movementType, quantityChange, previousStock, newStock);
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getMovementId() { return movementId; }
    public void setMovementId(Long movementId) { this.movementId = movementId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    
    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }
    
    public Integer getPreviousStock() { return previousStock; }
    public void setPreviousStock(Integer previousStock) { this.previousStock = previousStock; }
    
    public Integer getNewStock() { return newStock; }
    public void setNewStock(Integer newStock) { this.newStock = newStock; }
    
    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}