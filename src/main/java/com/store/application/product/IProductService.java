package com.store.application.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {
    Page<ProductDTO> getAllProducts(Pageable pageable);
    Optional<ProductDTO> getProductById(UUID id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO updatedProductDTO);
    void deleteProduct(UUID id);
    List<ProductDTO> getProductsByCategory(Category category);
    ProductDTO changePrice(UUID id, Double amount);
    ProductDTO increaseQuantity(UUID id, int amount);
}
