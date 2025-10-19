package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.Payment;
import com.dvpgiftcenter.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByTransactionTransactionId(Long transactionId);
    
    Payment findByTransaction(Transaction transaction);
}