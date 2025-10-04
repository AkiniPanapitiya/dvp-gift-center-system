package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByCustomerUserIdOrderByTransactionDateDesc(Long customerId);
    
    List<Transaction> findBySourceAndTransactionDateBetweenOrderByTransactionDateDesc(String source, 
                                                                                    LocalDateTime startDate, 
                                                                                    LocalDateTime endDate);
    
    Optional<Transaction> findByBillNumber(String billNumber);
    
    @Query("SELECT COUNT(t) + 1 FROM Transaction t WHERE DATE(t.transactionDate) = CURRENT_DATE")
    Long getNextBillSequenceForToday();
    
    @Query("SELECT t FROM Transaction t WHERE t.source = 'online_sale' AND t.customer.userId = :customerId ORDER BY t.transactionDate DESC")
    List<Transaction> findOnlineOrdersByCustomerId(@Param("customerId") Long customerId);
}