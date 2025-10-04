package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    
    List<CustomerAddress> findByCustomerUserId(Long customerId);
}