package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProductProductId(Long productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.minStockLevel")
    List<Inventory> findLowStockItems();
    
    @Modifying
    @Transactional
    @Query("UPDATE Inventory i SET i.currentStock = i.currentStock - :quantity WHERE i.product.productId = :productId")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Transactional
    @Query("UPDATE Inventory i SET i.currentStock = i.currentStock + :quantity WHERE i.product.productId = :productId")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}