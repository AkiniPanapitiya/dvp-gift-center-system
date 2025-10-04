package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.dto.product.OnlineProductDto;
import com.dvpgiftcenter.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/online")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @PostMapping("/cart/add")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @RequestBody @Valid AddToCartRequest request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            cartService.addToCart(username, request.getProductId(), request.getQuantity());
            
            return ResponseEntity.ok(
                ApiResponse.success("Product added to cart successfully", "Added to cart")
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to add to cart: " + e.getMessage()));
        }
    }
    
    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<OnlineProductDto>>> getCartItems(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<OnlineProductDto> cartItems = cartService.getCartItems(username);
            
            return ResponseEntity.ok(
                ApiResponse.success("Cart items retrieved successfully", cartItems)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get cart items: " + e.getMessage()));
        }
    }
    
    @PutMapping("/cart/update")
    public ResponseEntity<ApiResponse<String>> updateQuantity(
            @RequestBody @Valid UpdateCartRequest request,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            cartService.updateQuantity(username, request.getProductId(), request.getQuantity());
            
            return ResponseEntity.ok(
                ApiResponse.success("Cart updated successfully", "Updated")
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update cart: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            @PathVariable Long productId,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            cartService.removeFromCart(username, productId);
            
            return ResponseEntity.ok(
                ApiResponse.success("Product removed from cart successfully", "Removed")
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to remove from cart: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/cart/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(Authentication authentication) {
        try {
            String username = authentication.getName();
            cartService.clearCart(username);
            
            return ResponseEntity.ok(
                ApiResponse.success("Cart cleared successfully", "Cleared")
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to clear cart: " + e.getMessage()));
        }
    }
    
    @GetMapping("/cart/count")
    public ResponseEntity<ApiResponse<Integer>> getCartCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer count = cartService.getCartItemCount(username);
            
            return ResponseEntity.ok(
                ApiResponse.success("Cart count retrieved successfully", count)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get cart count: " + e.getMessage()));
        }
    }
    
    // Inner classes for request bodies
    public static class AddToCartRequest {
        private Long productId;
        private Integer quantity = 1;
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    public static class UpdateCartRequest {
        private Long productId;
        private Integer quantity;
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
