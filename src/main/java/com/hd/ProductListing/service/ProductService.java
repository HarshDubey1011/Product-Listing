package com.hd.ProductListing.service;

import com.hd.ProductListing.dto.ProductRequestDto;
import com.hd.ProductListing.dto.ProductResponseDto;
import com.hd.ProductListing.entity.Product;
import com.hd.ProductListing.exception.ProductNotFoundException;
import com.hd.ProductListing.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        Product product = ProductRequestDto.to(productRequestDto);
        Product prod = productRepository.save(product);

        return ProductResponseDto.from(prod);
    }

    public ProductResponseDto getProductById(Long id) {
        var prod = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        return ProductResponseDto.from(prod);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        var prod = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        ProductRequestDto.update(dto, prod);   // dirty checking persists it at commit
        productRepository.flush();
        return ProductResponseDto.from(prod);
    }

    public Page<ProductResponseDto> getAllProducts(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 10),
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
        Page<Product> products = (category == null || category.isBlank())
                ? productRepository.findAll(pageable)
                : productRepository.findByCategory(category, pageable);
        return ProductResponseDto.from(products);
    }

}
