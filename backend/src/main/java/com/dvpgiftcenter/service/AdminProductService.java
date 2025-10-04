package com.dvpgiftcenter.service;

import com.dvpgiftcenter.dto.admin.CreateProductRequest;
import com.dvpgiftcenter.dto.admin.UpdateProductRequest;
import com.dvpgiftcenter.dto.product.ProductDto;
import com.dvpgiftcenter.entity.Category;
import com.dvpgiftcenter.entity.Inventory;
import com.dvpgiftcenter.entity.OnlineProduct;
import com.dvpgiftcenter.entity.Product;
import com.dvpgiftcenter.repository.CategoryRepository;
import com.dvpgiftcenter.repository.InventoryRepository;
import com.dvpgiftcenter.repository.OnlineProductRepository;
import com.dvpgiftcenter.repository.ProductRepository;
import com.dvpgiftcenter.repository.TransactionItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private OnlineProductRepository onlineProductRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;
    
    public List<ProductDto> getAllProducts(String availability, boolean includeArchived) {
        // Normalize input to lower-case and trim
        String filter = availability != null ? availability.trim().toLowerCase() : null;
        List<Product> products;

        if ("online".equals(filter)) {
            // Only products available online
            products = includeArchived
                    ? productRepository.findAllByOnlineAvailableIncludeArchived(true)
                    : productRepository.findAllByIsActiveTrueAndOnlineAvailable(true);
        } else if ("offline".equals(filter)) {
            // Only products not available online
            products = includeArchived
                    ? productRepository.findAllByOnlineAvailableIncludeArchived(false)
                    : productRepository.findAllByIsActiveTrueAndOnlineAvailable(false);
        } else {
            // No availability filter
            products = includeArchived
                    ? productRepository.findAllOrderByProductName()
                    : productRepository.findByIsActiveTrueOrderByProductName();
        }

        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getAllActiveProducts() {
        List<Product> products = productRepository.findByIsActiveTrueOrderByProductName();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductDto> getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.map(this::convertToDto);
    }
    
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Category category = null;
        
        // Validate and find category by name if provided
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            category = categoryRepository.findByCategoryNameIgnoreCase(request.getCategory().trim())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategory()));
        }
        
        // Check for unique constraints
        if (request.getProductCode() != null && productRepository.existsByProductCode(request.getProductCode())) {
            throw new RuntimeException("Product code already exists: " + request.getProductCode());
        }
        
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new RuntimeException("Barcode already exists: " + request.getBarcode());
        }
        
        // Create product
        Product product = new Product();
        product.setProductName(request.getName());
        product.setProductCode(request.getProductCode());
        product.setBarcode(request.getBarcode());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(request.getIsActive());
        product.setCategory(category);
        
        // Save product first
        product = productRepository.save(product);
        
        // Create inventory record
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setCurrentStock(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        inventory.setMinStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : 0);
        inventory.setMaxStockLevel(request.getMaxStockLevel());
        
        inventoryRepository.save(inventory);
        
        // Create online product record if requested
        if (request.getIsAvailableOnline() != null && request.getIsAvailableOnline()) {
            OnlineProduct onlineProduct = new OnlineProduct();
            onlineProduct.setProduct(product);
            onlineProduct.setOnlinePrice(request.getOnlinePrice() != null ? request.getOnlinePrice() : request.getPrice());
            onlineProduct.setIsAvailableOnline(true);
            onlineProduct.setOnlineDescription(request.getOnlineDescription());
            onlineProduct.setPromotionalDetails(request.getPromotionalDetails());
            
            onlineProductRepository.save(onlineProduct);
        }
        
        // Refresh product to get inventory
        product = productRepository.findById(product.getProductId()).orElse(product);
        
        return convertToDto(product);
    }
    
    @Transactional
    public ProductDto updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        // Update product fields if provided
        if (request.getName() != null) {
            product.setProductName(request.getName());
        }
        if (request.getProductCode() != null) {
            // Check if different from current and doesn't exist elsewhere
            if (!request.getProductCode().equals(product.getProductCode()) && 
                productRepository.existsByProductCode(request.getProductCode())) {
                throw new RuntimeException("Product code already exists: " + request.getProductCode());
            }
            product.setProductCode(request.getProductCode());
        }
        if (request.getBarcode() != null) {
            // Check if different from current and doesn't exist elsewhere
            if (!request.getBarcode().equals(product.getBarcode()) && 
                productRepository.existsByBarcode(request.getBarcode())) {
                throw new RuntimeException("Barcode already exists: " + request.getBarcode());
            }
            product.setBarcode(request.getBarcode());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setUnitPrice(request.getPrice());
        }
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        
        // Update category if provided
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            Category category = categoryRepository.findAll().stream()
                    .filter(c -> c.getCategoryName().equalsIgnoreCase(request.getCategory().trim()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategory()));
            product.setCategory(category);
        }
        
        // Save product
        product = productRepository.save(product);
        
        // Update inventory if provided
        Inventory inventory = product.getInventory();
        if (inventory == null) {
            // Create inventory if doesn't exist
            inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setCurrentStock(0);
            inventory.setMinStockLevel(0);
        }
        
        if (request.getStockQuantity() != null) {
            inventory.setCurrentStock(request.getStockQuantity());
        }
        if (request.getMinStockLevel() != null) {
            inventory.setMinStockLevel(request.getMinStockLevel());
        }
        if (request.getMaxStockLevel() != null) {
            inventory.setMaxStockLevel(request.getMaxStockLevel());
        }
        
        inventoryRepository.save(inventory);
        
        // Handle online product updates
        OnlineProduct onlineProduct = product.getOnlineProduct();
        
        if (request.getIsAvailableOnline() != null) {
            if (request.getIsAvailableOnline()) {
                // Create online product if it doesn't exist
                if (onlineProduct == null) {
                    onlineProduct = new OnlineProduct();
                    onlineProduct.setProduct(product);
                    onlineProduct.setOnlinePrice(request.getOnlinePrice() != null ? request.getOnlinePrice() : product.getUnitPrice());
                    onlineProduct.setIsAvailableOnline(true);
                } else {
                    onlineProduct.setIsAvailableOnline(true);
                }
                
                // Update online product fields if provided
                if (request.getOnlinePrice() != null) {
                    onlineProduct.setOnlinePrice(request.getOnlinePrice());
                }
                if (request.getOnlineDescription() != null) {
                    onlineProduct.setOnlineDescription(request.getOnlineDescription());
                }
                if (request.getPromotionalDetails() != null) {
                    onlineProduct.setPromotionalDetails(request.getPromotionalDetails());
                }
                
                onlineProductRepository.save(onlineProduct);
            } else if (onlineProduct != null) {
                // Set online product as unavailable
                onlineProduct.setIsAvailableOnline(false);
                onlineProductRepository.save(onlineProduct);
            }
        } else if (onlineProduct != null) {
            // Update existing online product fields if provided
            if (request.getOnlinePrice() != null) {
                onlineProduct.setOnlinePrice(request.getOnlinePrice());
            }
            if (request.getOnlineDescription() != null) {
                onlineProduct.setOnlineDescription(request.getOnlineDescription());
            }
            if (request.getPromotionalDetails() != null) {
                onlineProduct.setPromotionalDetails(request.getPromotionalDetails());
            }
            
            onlineProductRepository.save(onlineProduct);
        }
        
        // Refresh product to get updated inventory and online product
        product = productRepository.findById(product.getProductId()).orElse(product);
        
        return convertToDto(product);
    }
    
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        // Soft delete by setting isActive to false
        product.setIsActive(false);
        // Also ensure any online listing is disabled
        if (product.getOnlineProduct() != null) {
            product.getOnlineProduct().setIsAvailableOnline(false);
        }
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId, boolean hardDelete) {
        if (!hardDelete) {
            deleteProduct(productId);
            return;
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // If any transaction item references this product, refuse hard delete
        if (transactionItemRepository.existsByProductProductId(productId)) {
            throw new IllegalStateException("Cannot hard delete: product has transaction history");
        }

        // Remove dependent rows first to satisfy FK constraints
        // OnlineProduct and Inventory are cascade=ALL from Product side via mappedBy; but to be explicit:
        if (product.getOnlineProduct() != null) {
            onlineProductRepository.delete(product.getOnlineProduct());
        }
        if (product.getInventory() != null) {
            inventoryRepository.delete(product.getInventory());
        }

        productRepository.delete(product);
    }
    
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getProductId());
        dto.setName(product.getProductName());
        dto.setProductCode(product.getProductCode());
        dto.setBarcode(product.getBarcode());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getUnitPrice());
        dto.setCostPrice(product.getCostPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsActive(product.getIsActive());
        
        if (product.getCategory() != null) {
            dto.setCategory(product.getCategory().getCategoryName());
            dto.setCategoryId(product.getCategory().getCategoryId());
        }
        
        if (product.getInventory() != null) {
            dto.setStockQuantity(product.getInventory().getCurrentStock());
            dto.setMinStockLevel(product.getInventory().getMinStockLevel());
            dto.setMaxStockLevel(product.getInventory().getMaxStockLevel());
        }
        
        // Map online product information
        if (product.getOnlineProduct() != null) {
            OnlineProduct onlineProduct = product.getOnlineProduct();
            dto.setIsAvailableOnline(onlineProduct.getIsAvailableOnline());
            dto.setOnlinePrice(onlineProduct.getOnlinePrice());
            dto.setOnlineDescription(onlineProduct.getOnlineDescription());
            dto.setPromotionalDetails(onlineProduct.getPromotionalDetails());
        } else {
            dto.setIsAvailableOnline(false);
            dto.setOnlinePrice(null);
            dto.setOnlineDescription(null);
            dto.setPromotionalDetails(null);
        }
        
        return dto;
    }

    @Transactional
    public ProductDto restoreProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        product.setIsActive(true);
        // Do not automatically enable online listing; keep previous online record as-is
        product = productRepository.save(product);

        return convertToDto(product);
    }
}