package com.dvpgiftcenter.service;

import com.dvpgiftcenter.dto.admin.CreateOnlineProductRequest;
import com.dvpgiftcenter.dto.admin.UpdateOnlineProductRequest;
import com.dvpgiftcenter.dto.product.OnlineProductDto;
import com.dvpgiftcenter.entity.OnlineProduct;
import com.dvpgiftcenter.entity.Product;
import com.dvpgiftcenter.repository.OnlineProductRepository;
import com.dvpgiftcenter.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminOnlineProductService {
    
    @Autowired
    private OnlineProductRepository onlineProductRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<OnlineProductDto> getAllOnlineProducts() {
        List<OnlineProduct> products = onlineProductRepository.findAllWithDetails();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OnlineProductDto createOnlineProduct(CreateOnlineProductRequest request) {
        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.getProductId()));
        
        // Check if online product already exists for this product
        if (onlineProductRepository.existsByProductProductId(request.getProductId())) {
            throw new RuntimeException("Online product already exists for this product");
        }
        
        // Create online product
        OnlineProduct onlineProduct = new OnlineProduct(product, request.getOnlinePrice());
        onlineProduct.setOnlineDescription(request.getOnlineDescription());
        onlineProduct.setPromotionalDetails(request.getPromotionalDetails());
        onlineProduct.setIsAvailableOnline(request.getIsAvailableOnline());
        
        onlineProduct = onlineProductRepository.save(onlineProduct);
        return convertToDto(onlineProduct);
    }
    
    @Transactional
    public OnlineProductDto updateOnlineProduct(Long productId, UpdateOnlineProductRequest request) {
        OnlineProduct onlineProduct = onlineProductRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Online product not found for product ID: " + productId));
        
        // Update fields if provided
        if (request.getOnlinePrice() != null) {
            onlineProduct.setOnlinePrice(request.getOnlinePrice());
        }
        if (request.getOnlineDescription() != null) {
            onlineProduct.setOnlineDescription(request.getOnlineDescription());
        }
        if (request.getPromotionalDetails() != null) {
            onlineProduct.setPromotionalDetails(request.getPromotionalDetails());
        }
        if (request.getIsAvailableOnline() != null) {
            onlineProduct.setIsAvailableOnline(request.getIsAvailableOnline());
        }
        
        onlineProduct = onlineProductRepository.save(onlineProduct);
        return convertToDto(onlineProduct);
    }
    
    @Transactional
    public void deleteOnlineProduct(Long productId) {
        OnlineProduct onlineProduct = onlineProductRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Online product not found for product ID: " + productId));
        
        // Soft delete by setting isAvailableOnline to false
        onlineProduct.setIsAvailableOnline(false);
        onlineProductRepository.save(onlineProduct);
    }
    
    public Optional<OnlineProductDto> getOnlineProductById(Long productId) {
        Optional<OnlineProduct> product = onlineProductRepository.findByProductId(productId);
        return product.map(this::convertToDto);
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