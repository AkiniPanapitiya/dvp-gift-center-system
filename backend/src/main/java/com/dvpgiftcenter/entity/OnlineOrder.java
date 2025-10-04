package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ONLINE_ORDERS")
public class OnlineOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", unique = true, nullable = false)
    private Transaction transaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private CustomerAddress shippingAddress;
    
    @Size(max = 50)
    @Column(name = "order_status")
    private String orderStatus;
    
    @Size(max = 50)
    @Column(name = "shipping_method")
    private String shippingMethod;
    
    @Size(max = 100)
    @Column(name = "tracking_number")
    private String trackingNumber;
    
    @CreationTimestamp
    @Column(name = "placed_at")
    private LocalDateTime placedAt;
    
    // Constructors
    public OnlineOrder() {}
    
    public OnlineOrder(User customer, Transaction transaction, CustomerAddress shippingAddress, String orderStatus) {
        this.customer = customer;
        this.transaction = transaction;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
    }
    
    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    
    public CustomerAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(CustomerAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    
    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }
}