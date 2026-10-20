package com.hd.ProductListing.dto;

import com.hd.ProductListing.entity.Product;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stockQuantity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ProductResponseDto from(Product prod) {
        return new ProductResponseDto(
                prod.getId(),
                prod.getName(),
                prod.getDescription(),
                prod.getPrice(),
                prod.getCategory(),
                prod.getStockQuantity(),
                prod.getCreatedAt(),
                prod.getUpdatedAt()
        );
    }

    public static Page<ProductResponseDto> from(Page<Product> products) {
        return products.map(ProductResponseDto::from);
    }
}
