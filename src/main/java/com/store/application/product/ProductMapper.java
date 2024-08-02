package com.store.application.product;

import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(String.valueOf(product.getCategory()))
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .discount(product.getDiscount())
                .build();
    }

    public Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .category(Category.valueOf(productDTO.getCategory()))
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .discount(productDTO.getDiscount())
                .build();
    }
}
