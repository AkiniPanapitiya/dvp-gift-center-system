package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {
    
    List<TransactionItem> findByTransactionTransactionId(Long transactionId);

    // Check if any transaction item references the given product id
    boolean existsByProductProductId(Long productId);
}