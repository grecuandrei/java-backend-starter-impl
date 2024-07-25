package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new ProductAlreadyExistsException("Product already exists with name: " + product.getName());
        }
        return productRepository.save(product);
    }

    public Product updateProduct(UUID id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> productRepository.save(product)).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public Product increaseQuantity(UUID id, int amount) {
        return productRepository.findById(id).map(product -> {
            product.setQuantity(product.getQuantity() + amount);
            return productRepository.save(product);
        }).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }
}
