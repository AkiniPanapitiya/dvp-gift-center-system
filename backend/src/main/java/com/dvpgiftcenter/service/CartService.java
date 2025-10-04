package com.dvpgiftcenter.service;

import com.dvpgiftcenter.dto.product.OnlineProductDto;
import com.dvpgiftcenter.entity.CartItem;
import com.dvpgiftcenter.entity.Product;
import com.dvpgiftcenter.entity.User;
import com.dvpgiftcenter.repository.CartItemRepository;
import com.dvpgiftcenter.repository.ProductRepository;
import com.dvpgiftcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OnlineProductService onlineProductService;
    
    @Transactional
    public void addToCart(String username, Long productId, Integer quantity) {
        User customer = userRepository.findByUsernameAndIsActiveTrue(username)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Optional<CartItem> existingItem = cartItemRepository
            .findByCustomerUserIdAndProductProductId(customer.getUserId(), productId);
        
        if (existingItem.isPresent()) {
            // Update existing item
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem(customer, product, quantity);
            cartItemRepository.save(cartItem);
        }
    }
    
    @Transactional
    public void updateQuantity(String username, Long productId, Integer quantity) {
        User customer = userRepository.findByUsernameAndIsActiveTrue(username)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if (quantity <= 0) {
            removeFromCart(username, productId);
            return;
        }
        
        CartItem cartItem = cartItemRepository
            .findByCustomerUserIdAndProductProductId(customer.getUserId(), productId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
    
    @Transactional
    public void removeFromCart(String username, Long productId) {
        User customer = userRepository.findByUsernameAndIsActiveTrue(username)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        cartItemRepository.deleteByCustomerUserIdAndProductProductId(
            customer.getUserId(), productId);
    }
    
    @Transactional(readOnly = true)
    public List<OnlineProductDto> getCartItems(String username) {
        User customer = userRepository.findByUsernameAndIsActiveTrue(username)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        List<CartItem> cartItems = cartItemRepository
            .findByCustomerUserIdOrderByAddedAtDesc(customer.getUserId());
        
        return cartItems.stream()
            .map(cartItem -> {
                Optional<OnlineProductDto> productDto = onlineProductService
                    .getProductById(cartItem.getProduct().getProductId());
                
                if (productDto.isPresent()) {
                    OnlineProductDto dto = productDto.get();
                    // Include cart quantity in the DTO so client can render persisted cart
                    dto.setQuantity(cartItem.getQuantity());
                    return dto;
                } else {
                    throw new RuntimeException("Product not found: " + cartItem.getProduct().getProductId());
                }
            })
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void clearCart(String username) {
        User customer = userRepository.findByUsernameAndIsActiveTrue(username)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        cartItemRepository.deleteByCustomerId(customer.getUserId());
    }
    
    public Integer getCartItemCount(String username) {
        User customer = userRepository.findByUsernameAndIsActiveTrue(username)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return cartItemRepository.getTotalQuantityByCustomerId(customer.getUserId());
    }
}
