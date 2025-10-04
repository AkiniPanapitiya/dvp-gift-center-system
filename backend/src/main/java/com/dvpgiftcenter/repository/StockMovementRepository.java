package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByProductProductIdOrderByMovementDateDesc(Long productId);
    
    List<StockMovement> findByTransactionTransactionId(Long transactionId);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate ORDER BY sm.movementDate DESC")
    List<StockMovement> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementType = :movementType ORDER BY sm.movementDate DESC")
    List<StockMovement> findByMovementType(@Param("movementType") String movementType);
}