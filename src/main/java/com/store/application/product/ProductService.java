package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(UUID id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id).map(productMapper::toDTO);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.getName());
        if (productRepository.findByName(productDTO.getName()).isPresent()) {
            log.error("Product with name {} already exists", productDTO.getName());
            throw new ProductAlreadyExistsException("Product already exists with name: " + productDTO.getName());
        }
        Product product = productMapper.toEntity(productDTO);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO updatedProductDTO) {
        log.info("Updating product with id: {}", updatedProductDTO.getId());
        return productRepository.findById(updatedProductDTO.getId()).map(product -> {
            Optional<Product> existingProduct = productRepository.findByName(updatedProductDTO.getName());
            if (existingProduct.isPresent() && !existingProduct.get().getId().equals(updatedProductDTO.getId())) {
                log.error("Product with name {} already exists", updatedProductDTO.getName());
                throw new ProductAlreadyExistsException("Product already exists with name: " + updatedProductDTO.getName());
            }
            product.setName(updatedProductDTO.getName());
            product.setDescription(updatedProductDTO.getDescription());
            product.setCategory(Category.valueOf(updatedProductDTO.getCategory()));
            product.setPrice(updatedProductDTO.getPrice());
            product.setQuantity(updatedProductDTO.getQuantity());
            product.setDiscount(updatedProductDTO.getDiscount());
            log.info("Updated product: {}", product);
            return productMapper.toDTO(productRepository.save(product));
        }).orElseThrow(() -> {
            log.error("Product not found with id: {}", updatedProductDTO.getId());
            return new ProductNotFoundException("Product not found with id: " + updatedProductDTO.getId());
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

    public List<ProductDTO> getProductsByCategory(Category category) {
        log.info("Fetching all products by category: {}", category);
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO changePrice(UUID id, Double amount) {
        log.info("Changing price of product with id: {}", id);
        return productRepository.findById(id).map(product -> {
            product.setPrice(amount);
            log.info("Changed price of product: {}", product);
            return productMapper.toDTO(productRepository.save(product));
        }).orElseThrow(() -> {
            log.error("Product not found with id: {}", id);
            return new ProductNotFoundException("Product not found with id: " + id);
        });
    }

    @Transactional
    public ProductDTO increaseQuantity(UUID id, int amount) {
        log.info("Increasing quantity of product with id: {}", id);
        return productRepository.findById(id).map(product -> {
            product.setQuantity(product.getQuantity() + amount);
            log.info("Increased quantity of product: {}", product);
            return productMapper.toDTO(productRepository.save(product));
        }).orElseThrow(() -> {
            log.error("Product not found with id: {}", id);
            return new ProductNotFoundException("Product not found with id: " + id);
        });
    }

    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
