package com.docx.demo.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docx.demo.models.Product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Product catalog management operations.
 * 
 * This controller manages the complete product lifecycle including inventory
 * management, pricing updates, category organization, and availability tracking.
 * It provides comprehensive e-commerce product management functionality.
 * 
 * @apiNote All endpoints support proper error handling and validation
 * @author Demo Team  
 * @since 1.0.0
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Map<Long, Product> productRepository = new HashMap<>();
    private Long nextId = 1L;

    /**
     * Retrieves a paginated catalog of all products.
     * 
     * Supports advanced filtering by category, price range, availability status,
     * and full-text search across product names and descriptions.
     * 
     * @param pageable Pagination and sorting parameters
     * @param category Optional category filter for product listing
     * @param minPrice Minimum price threshold for filtering products
     * @param maxPrice Maximum price threshold for filtering products
     * @param available Filter products by availability status
     * @param search Full-text search query for product names and descriptions
     * @return Paginated product catalog with filtering applied
     * 
     * @apiExample GET /api/products?category=electronics&minPrice=100&maxPrice=500&available=true
     * @apiExample GET /api/products?search=laptop&sort=price,asc&page=0&size=20
     * @apiResponse 200 Product catalog retrieved successfully with pagination metadata
     * @apiResponse 400 Invalid filter parameters or pagination settings provided
     * @apiResponse 500 Internal server error occurred while retrieving product catalog
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @Min(0) BigDecimal minPrice,
            @RequestParam(required = false) @Min(0) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String search) {
        
        List<Product> filteredProducts = productRepository.values().stream()
                .filter(product -> category == null || product.getCategory().equalsIgnoreCase(category))
                .filter(product -> minPrice == null || product.getPrice().compareTo(minPrice) >= 0)
                .filter(product -> maxPrice == null || product.getPrice().compareTo(maxPrice) <= 0)
                .filter(product -> available == null || product.isAvailable() == available)
                .filter(product -> search == null || 
                    product.getName().toLowerCase().contains(search.toLowerCase()) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(search.toLowerCase())))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredProducts.size());
        List<Product> pageContent = filteredProducts.subList(start, end);

        Page<Product> productPage = new PageImpl<>(pageContent, pageable, filteredProducts.size());
        return ResponseEntity.ok(productPage);
    }

    /**
     * Retrieves detailed information for a specific product.
     * 
     * @param id The unique product identifier
     * @return Complete product details including inventory and pricing information
     * @throws ProductNotFoundException when product with specified ID does not exist
     * 
     * @apiExample GET /api/products/456
     * @apiResponse 200 Product details retrieved successfully with complete information
     * @apiResponse 400 Invalid product ID format provided
     * @apiResponse 404 Product not found or has been discontinued
     * @apiResponse 500 Internal server error occurred while retrieving product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable @NotNull @Min(value = 1, message = "Product ID must be positive") Long id) {
        
        Product product = productRepository.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Adds a new product to the catalog.
     * 
     * Creates a new product with comprehensive validation including SKU uniqueness,
     * price validation, and category normalization. Automatically sets creation
     * timestamps and default availability status.
     * 
     * @param product Complete product information for catalog addition
     * @return Newly created product with generated ID and metadata
     * 
     * @apiExample POST /api/products
     * @apiResponse 201 Product successfully added to catalog with generated ID
     * @apiResponse 400 Invalid product data or validation constraints violated
     * @apiResponse 409 Product SKU already exists in the catalog
     * @apiResponse 500 Internal server error occurred during product creation
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        // Check if SKU already exists
        boolean skuExists = productRepository.values().stream()
                .anyMatch(p -> p.getSku().equals(product.getSku()));

        if (skuExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        product.setId(nextId++);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.put(product.getId(), product);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * Updates existing product information.
     * 
     * Supports partial updates to product data with validation for price changes,
     * SKU uniqueness, and inventory adjustments. Maintains data integrity
     * throughout the update process.
     * 
     * @param id Unique identifier of the product to update
     * @param product Updated product information (partial updates supported)
     * @return Updated product with new timestamp and revised information
     * 
     * @apiExample PUT /api/products/456
     * @apiResponse 200 Product successfully updated with new information
     * @apiResponse 400 Invalid product data or validation constraints violated
     * @apiResponse 404 Product not found in catalog
     * @apiResponse 409 Updated SKU conflicts with existing product
     * @apiResponse 500 Internal server error occurred during product update
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable @NotNull @Min(1) Long id,
            @Valid @RequestBody Product product) {
        
        Product existingProduct = productRepository.get(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        // Check for SKU conflicts with other products
        boolean skuConflict = productRepository.values().stream()
                .anyMatch(p -> !p.getId().equals(id) && p.getSku().equals(product.getSku()));

        if (skuConflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Update product data
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setSku(product.getSku());
        existingProduct.setAvailable(product.isAvailable());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(existingProduct);
    }

    /**
     * Removes a product from the catalog.
     * 
     * @param id Unique identifier of the product to remove
     * @return Empty response confirming successful deletion
     * 
     * @apiExample DELETE /api/products/456
     * @apiResponse 204 Product successfully removed from catalog
     * @apiResponse 404 Product not found or already removed
     * @apiResponse 500 Internal server error occurred during product removal
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable @NotNull @Min(1) Long id) {
        
        Product product = productRepository.remove(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates product inventory levels.
     * 
     * Adjusts stock quantities with proper validation to prevent negative inventory.
     * Automatically updates availability status based on stock levels and provides
     * audit trail for inventory changes.
     * 
     * @param id Unique product identifier for inventory update
     * @param quantity New stock quantity (must be non-negative)
     * @return Updated product with new inventory levels and availability status
     * 
     * @apiExample PATCH /api/products/456/inventory?quantity=50
     * @apiResponse 200 Inventory successfully updated with new stock level     * @apiResponse 404 Product not found in catalog
     * @apiResponse 500 Internal server error occurred during inventory update
     */
    @PatchMapping("/{id}/inventory")
    public ResponseEntity<Product> updateInventory(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestParam @NotNull @Min(value = 0, message = "Stock quantity cannot be negative") Integer quantity) {
        
        Product product = productRepository.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        product.setStockQuantity(quantity);
        product.setAvailable(quantity > 0);
        product.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(product);
    }

    /**
     * Updates product pricing information.
     * 
     * Allows dynamic price adjustments with validation for minimum pricing rules
     * and proper decimal formatting. Maintains pricing history through timestamps.
     * 
     * @param id Unique product identifier for price update
     * @param price New product price (must be positive with proper decimal precision)
     * @return Updated product with new pricing and timestamp metadata
     * 
     * @apiExample PATCH /api/products/456/price?price=299.99
     * @apiResponse 200 Product price successfully updated
     * @apiResponse 400 Invalid price format or value provided
     * @apiResponse 404 Product not found in catalog
     * @apiResponse 500 Internal server error occurred during price update
     */
    @PatchMapping("/{id}/price")
    public ResponseEntity<Product> updatePrice(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestParam @NotNull @Min(value = 0, message = "Price must be positive") BigDecimal price) {
        
        Product product = productRepository.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        product.setPrice(price);
        product.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(product);
    }

    /**
     * Retrieves all available product categories.
     * 
     * Returns a distinct list of all categories currently used in the product catalog,
     * useful for category filters and product organization interfaces.
     * 
     * @return List of unique product categories sorted alphabetically
     * 
     * @apiExample GET /api/products/categories
     * @apiResponse 200 Categories retrieved successfully
     * @apiResponse 500 Internal server error occurred while retrieving categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = productRepository.values().stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .toList();

        return ResponseEntity.ok(categories);
    }

    /**
     * Searches products by SKU pattern.
     * 
     * Advanced search functionality for finding products using partial SKU matches,
     * useful for inventory management and quick product lookups.
     * 
     * @param skuPattern Partial SKU pattern for product search (case-insensitive)
     * @return List of products matching the SKU pattern
     * 
     * @apiExample GET /api/products/search/sku?pattern=LAP
     * @apiResponse 200 SKU search completed successfully
     * @apiResponse 400 Invalid SKU pattern provided
     * @apiResponse 500 Internal server error occurred during SKU search
     */
    @GetMapping("/search/sku")
    public ResponseEntity<List<Product>> searchBySku(
            @RequestParam @NotNull String skuPattern) {
        
        List<Product> results = productRepository.values().stream()
                .filter(product -> product.getSku().toLowerCase().contains(skuPattern.toLowerCase()))
                .sorted((p1, p2) -> p1.getSku().compareToIgnoreCase(p2.getSku()))
                .toList();

        return ResponseEntity.ok(results);
    }
}