package com.hd.ProductListing.dto;

import com.hd.ProductListing.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        @NotBlank(message = "Category is required")
        String category,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock cannot be negative")
        Integer stockQuantity
) {
    public static Product update(ProductRequestDto productRequestDto, Product product) {
        product.setCategory(productRequestDto.category());
        product.setPrice(productRequestDto.price());
        product.setDescription(productRequestDto.description());
        product.setName(productRequestDto.name());
        product.setStockQuantity(productRequestDto.stockQuantity());
        return product;
    }

    public static Product to(ProductRequestDto productRequestDto) {
        Product product = new Product();
        return update(productRequestDto, product);
    }
}
