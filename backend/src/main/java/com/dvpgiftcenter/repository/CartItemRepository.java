package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Find all cart items for a customer
    List<CartItem> findByCustomerUserIdOrderByAddedAtDesc(Long customerId);
    
    // Find specific cart item by customer and product
    Optional<CartItem> findByCustomerUserIdAndProductProductId(Long customerId, Long productId);
    
    // Delete all cart items for a customer (when order is completed)
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.customer.userId = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);
    
    // Delete specific cart item
    @Modifying
    @Transactional
    void deleteByCustomerUserIdAndProductProductId(Long customerId, Long productId);
    
    // Count cart items for a customer
    long countByCustomerUserId(Long customerId);
    
    // Get total quantity in cart for a customer
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.customer.userId = :customerId")
    Integer getTotalQuantityByCustomerId(@Param("customerId") Long customerId);
}
