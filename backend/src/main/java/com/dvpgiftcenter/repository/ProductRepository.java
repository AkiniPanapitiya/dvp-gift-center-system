package com.dvpgiftcenter.repository;

import com.dvpgiftcenter.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByIsActiveTrueOrderByProductName();
    
    List<Product> findByCategoryCategoryIdAndIsActiveTrueOrderByProductName(Integer categoryId);
    
    List<Product> findByProductNameContainingIgnoreCaseAndIsActiveTrueOrderByProductName(String productName);
    
    // Search by product name, code, or barcode for POS system
    List<Product> findByProductNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrBarcodeContainingIgnoreCase(
        String productName, String productCode, String barcode);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(:categoryId IS NULL OR p.category.categoryId = :categoryId) AND " +
           "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
           "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice) " +
           "ORDER BY p.productName")
    List<Product> findProductsWithFilters(@Param("categoryId") Integer categoryId,
                                        @Param("productName") String productName,
                                        @Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice);
    
    Optional<Product> findByProductCodeAndIsActiveTrue(String productCode);
    
    Optional<Product> findByBarcodeAndIsActiveTrue(String barcode);
    
    boolean existsByProductCode(String productCode);
    
    boolean existsByBarcode(String barcode);

    // Filter by online availability using LEFT JOIN so products without an OnlineProduct row are treated as offline
    @Query("SELECT p FROM Product p LEFT JOIN p.onlineProduct op " +
           "WHERE p.isActive = true AND ((:available = true AND op.isAvailableOnline = true) " +
           "OR (:available = false AND (op IS NULL OR op.isAvailableOnline = false))) " +
           "ORDER BY p.productName")
    List<Product> findAllByIsActiveTrueAndOnlineAvailable(@Param("available") boolean available);

    // Same as above but without filtering by isActive to include archived products
    @Query("SELECT p FROM Product p LEFT JOIN p.onlineProduct op " +
           "WHERE ((:available = true AND op.isAvailableOnline = true) " +
           "OR (:available = false AND (op IS NULL OR op.isAvailableOnline = false))) " +
           "ORDER BY p.productName")
    List<Product> findAllByOnlineAvailableIncludeArchived(@Param("available") boolean available);

    // List all products ordered by name (including archived)
    @Query("SELECT p FROM Product p ORDER BY p.productName")
    List<Product> findAllOrderByProductName();
}