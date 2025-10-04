package com.dvpgiftcenter.service;

import com.dvpgiftcenter.dto.category.CategoryDto;
import com.dvpgiftcenter.dto.product.OnlineProductDto;
import com.dvpgiftcenter.entity.Category;
import com.dvpgiftcenter.entity.OnlineProduct;
import com.dvpgiftcenter.repository.CategoryRepository;
import com.dvpgiftcenter.repository.OnlineProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OnlineProductService {
    
    @Autowired
    private OnlineProductRepository onlineProductRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<OnlineProductDto> getAllAvailableProducts() {
        List<OnlineProduct> products = onlineProductRepository.findAllAvailableOnlineProducts();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<OnlineProductDto> searchProducts(Integer categoryId, String productName, 
                                                BigDecimal minPrice, BigDecimal maxPrice) {
        List<OnlineProduct> products = onlineProductRepository.findOnlineProductsWithFilters(
            categoryId, productName, minPrice, maxPrice);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<OnlineProductDto> getProductById(Long productId) {
        Optional<OnlineProduct> product = onlineProductRepository.findByProductId(productId);
        return product.map(this::convertToDto);
    }
    
    public List<CategoryDto> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrueOrderByCategoryName();
        return categories.stream()
                .map(this::convertCategoryToDto)
                .collect(Collectors.toList());
    }
    
    private CategoryDto convertCategoryToDto(Category category) {
        return new CategoryDto(
            category.getCategoryId(),
            category.getCategoryName(),
            category.getDescription(),
            category.getIsActive()
        );
    }
    
    private OnlineProductDto convertToDto(OnlineProduct onlineProduct) {
        OnlineProductDto dto = new OnlineProductDto();
        dto.setProductId(onlineProduct.getProduct().getProductId());
        dto.setProductName(onlineProduct.getProduct().getProductName());
        dto.setDescription(onlineProduct.getProduct().getDescription());
        dto.setOnlineDescription(onlineProduct.getOnlineDescription());
        dto.setOnlinePrice(onlineProduct.getOnlinePrice());
        dto.setImageUrl(onlineProduct.getProduct().getImageUrl());
        dto.setPromotionalDetails(onlineProduct.getPromotionalDetails());
        
        if (onlineProduct.getProduct().getCategory() != null) {
            dto.setCategoryName(onlineProduct.getProduct().getCategory().getCategoryName());
            dto.setCategoryId(onlineProduct.getProduct().getCategory().getCategoryId());
        }
        
        if (onlineProduct.getProduct().getInventory() != null) {
            dto.setCurrentStock(onlineProduct.getProduct().getInventory().getCurrentStock());
        }
        
        return dto;
    }
}