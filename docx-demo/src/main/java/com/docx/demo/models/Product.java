package com.docx.demo.models;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity representing an e-commerce product.
 * 
 * @author Demo Team
 * @since 1.0.0
 */
public class Product {

    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Price must have at most 6 integer digits and 2 decimal places")
    private BigDecimal price;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "SKU cannot be blank")
    @Pattern(regexp = "^[A-Z0-9]{3,20}$", message = "SKU must be 3-20 characters long and contain only uppercase letters and numbers")
    private String sku;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean available;

    public Product() {
        this.available = true;
        this.stockQuantity = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Product(String name, BigDecimal price, String category, String sku) {
        this();
        this.name = name;
        this.price = price;
        this.category = category;
        this.sku = sku;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}