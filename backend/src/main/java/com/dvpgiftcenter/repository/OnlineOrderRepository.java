package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.OnlineOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnlineOrderRepository extends JpaRepository<OnlineOrder, Long> {
    
    List<OnlineOrder> findByCustomerUserIdOrderByPlacedAtDesc(Long customerId);
    
    @Query("SELECT oo FROM OnlineOrder oo " +
           "JOIN FETCH oo.transaction t " +
           "JOIN FETCH t.transactionItems ti " +
           "JOIN FETCH ti.product p " +
           "WHERE oo.customer.userId = :customerId " +
           "ORDER BY oo.placedAt DESC")
    List<OnlineOrder> findByCustomerIdWithDetails(@Param("customerId") Long customerId);
    
    Optional<OnlineOrder> findByTransactionTransactionId(Long transactionId);
    
    List<OnlineOrder> findByOrderStatusOrderByPlacedAtDesc(String orderStatus);
}