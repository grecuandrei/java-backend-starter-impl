package com.store.application.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {
    List<ProductDTO> getAllProducts();
    Optional<ProductDTO> getProductById(UUID id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO updatedProductDTO);
    void deleteProduct(UUID id);
    List<ProductDTO> getProductsByCategory(Category category);
    ProductDTO changePrice(UUID id, Double amount);
    ProductDTO increaseQuantity(UUID id, int amount);
}
