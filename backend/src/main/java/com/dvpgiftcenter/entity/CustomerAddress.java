package com.dvpgiftcenter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CUSTOMER_ADDRESSES")
public class CustomerAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Size(max = 255)
    @Column(name = "address_line2")
    private String addressLine2;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "city")
    private String city;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "postal_code")
    private String postalCode;
    
    // Constructors
    public CustomerAddress() {}
    
    public CustomerAddress(User customer, String addressLine1, String city, String postalCode) {
        this.customer = customer;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.postalCode = postalCode;
    }
    
    // Getters and Setters
    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}