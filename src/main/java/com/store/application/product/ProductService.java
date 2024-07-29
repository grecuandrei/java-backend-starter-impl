package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(UUID id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product);
        if (productRepository.findByName(product.getName()).isPresent()) {
            log.error("Product with name {} already exists", product.getName());
            throw new ProductAlreadyExistsException("Product already exists with name: " + product.getName());
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Product updatedProduct) {
        log.info("Updating product with id: {}", updatedProduct.getId());
        return productRepository.findById(updatedProduct.getId()).map(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setCategory(updatedProduct.getCategory());
            product.setPrice(updatedProduct.getPrice());
            product.setQuantity(updatedProduct.getQuantity());
            product.setDiscount(updatedProduct.getDiscount());
            log.info("Updated product: {}", product);
            return productRepository.save(product);
        }).orElseThrow(() -> {
            log.error("Product not found with id: {}", updatedProduct.getId());
            return new ProductNotFoundException("Product not found with id: " + updatedProduct.getId());
        });
    }

    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            log.error("Product not found with id: {}", id);
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(Category category) {
        log.info("Fetching product with category: {}", category.name());
        return productRepository.findByCategory(category);
    }

    @Transactional
    public Product changePrice(UUID id, Double amount) {
        log.info("Changing price for product with id: {}", id);
        return productRepository.findById(id).map(product -> {
            product.setPrice(amount);
            return productRepository.save(product);
        }).orElseThrow(() -> {
            log.error("Product not found with id: {}", id);
            return new ProductNotFoundException("Product not found with id: " + id);
        });
    }

    @Transactional
    public Product increaseQuantity(UUID id, int amount) {
        log.info("Increasing quantity for product with id: {}", id);
        return productRepository.findById(id).map(product -> {
            product.setQuantity(product.getQuantity() + amount);
            return productRepository.save(product);
        }).orElseThrow(() -> {
            log.error("Product not found with id: {}", id);
            return new ProductNotFoundException("Product not found with id: " + id);
        });
    }
}
