package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.OnlineProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnlineProductRepository extends JpaRepository<OnlineProduct, Long> {
    
    @Query("SELECT op FROM OnlineProduct op " +
           "JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.inventory i " +
           "WHERE op.isAvailableOnline = true AND p.isActive = true " +
           "ORDER BY p.productName")
    List<OnlineProduct> findAllAvailableOnlineProducts();
    
    @Query("SELECT op FROM OnlineProduct op " +
           "JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.inventory i " +
           "WHERE op.isAvailableOnline = true AND p.isActive = true AND " +
           "(:categoryId IS NULL OR p.category.categoryId = :categoryId) AND " +
           "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
           "(:minPrice IS NULL OR op.onlinePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR op.onlinePrice <= :maxPrice) " +
           "ORDER BY p.productName")
    List<OnlineProduct> findOnlineProductsWithFilters(@Param("categoryId") Integer categoryId,
                                                     @Param("productName") String productName,
                                                     @Param("minPrice") BigDecimal minPrice,
                                                     @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT op FROM OnlineProduct op " +
           "JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.inventory i " +
           "WHERE p.productId = :productId")
    Optional<OnlineProduct> findByProductId(@Param("productId") Long productId);
    
    Optional<OnlineProduct> findByProductProductId(Long productId);
    
    @Query("SELECT op FROM OnlineProduct op " +
           "JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.inventory i " +
           "ORDER BY p.productName")
    List<OnlineProduct> findAllWithDetails();
    
    boolean existsByProductProductId(Long productId);
}