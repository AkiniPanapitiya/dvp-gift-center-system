package com.dvpgiftcenter.dto.checkout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CheckoutRequest {
    
    @NotEmpty
    private List<CheckoutItem> items;
    
    @NotNull
    private ShippingAddress shippingAddress;
    
    @NotBlank
    @Size(max = 50)
    private String paymentMethod;
    
    private String shippingMethod;
    
    // Inner class for checkout items
    public static class CheckoutItem {
        @NotNull
        private Long productId;
        
        @NotNull
        private Integer quantity;
        
        // Constructors
        public CheckoutItem() {}
        
        public CheckoutItem(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    // Inner class for shipping address
    public static class ShippingAddress {
        @NotBlank
        @Size(max = 255)
        private String addressLine1;
        
        @Size(max = 255)
        private String addressLine2;
        
        @NotBlank
        @Size(max = 100)
        private String city;
        
        @NotBlank
        @Size(max = 20)
        private String postalCode;
        
        // Constructors
        public ShippingAddress() {}
        
        public ShippingAddress(String addressLine1, String city, String postalCode) {
            this.addressLine1 = addressLine1;
            this.city = city;
            this.postalCode = postalCode;
        }
        
        // Getters and Setters
        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
        
        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }
    
    // Constructors
    public CheckoutRequest() {}
    
    // Getters and Setters
    public List<CheckoutItem> getItems() { return items; }
    public void setItems(List<CheckoutItem> items) { this.items = items; }
    
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
}