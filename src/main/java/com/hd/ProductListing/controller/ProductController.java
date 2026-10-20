package com.hd.ProductListing.controller;

import com.hd.ProductListing.dto.ApiResponse;
import com.hd.ProductListing.dto.ProductRequestDto;
import com.hd.ProductListing.dto.ProductResponseDto;
import com.hd.ProductListing.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductRequestDto request) {
        var product = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/api/v1/products/" + product.id())).body(ApiResponse.success("Product created successfully", product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(@PathVariable Long id) {
        var product = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Product found successfully", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDto request) {
        var product = productService.updateProduct(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Product updated successfully", product));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) int size,
            @RequestParam(required = false) String category
    ) {
        var productList = productService.getAllProducts(page, size, category);
        return ResponseEntity.ok(ApiResponse.success("All Products", productList));
    }
}
