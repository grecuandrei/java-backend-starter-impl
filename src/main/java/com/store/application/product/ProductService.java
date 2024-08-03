package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import com.store.application.utils.LogMessages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Cacheable(cacheNames = "products", unless = "#result == null")
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.info(LogMessages.FETCHING_ALL_PRODUCTS);
        return productRepository.findAll(pageable).map(productMapper::toDTO);
    }


    @Cacheable(cacheNames = "products", key = "#id", unless = "#result == null")
    public Optional<ProductDTO> getProductById(UUID id) {
        log.info(LogMessages.FETCHING_PRODUCT + "{}", id);
        return productRepository.findById(id).map(productMapper::toDTO);
    }

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info(LogMessages.CREATING_PRODUCT + "{}", productDTO.getName());
        if (productRepository.findByName(productDTO.getName()).isPresent()) {
            log.error(LogMessages.PRODUCT_ALREADY_EXISTS + "{}", productDTO.getName());
            throw new ProductAlreadyExistsException(LogMessages.PRODUCT_ALREADY_EXISTS + productDTO.getName());
        }
        Product product = productMapper.toEntity(productDTO);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductDTO updateProduct(ProductDTO updatedProductDTO) {
        log.info(LogMessages.UPDATING_PRODUCT + "{}", updatedProductDTO.getId());
        return productRepository.findById(updatedProductDTO.getId()).map(product -> {
            Optional<Product> existingProduct = productRepository.findByName(updatedProductDTO.getName());
            if (existingProduct.isPresent() && !existingProduct.get().getId().equals(updatedProductDTO.getId())) {
                log.error(LogMessages.PRODUCT_ALREADY_EXISTS + "{}", updatedProductDTO.getName());
                throw new ProductAlreadyExistsException(LogMessages.PRODUCT_ALREADY_EXISTS + updatedProductDTO.getName());
            }
            product.setName(updatedProductDTO.getName());
            product.setDescription(updatedProductDTO.getDescription());
            product.setCategory(Category.valueOf(updatedProductDTO.getCategory()));
            product.setPrice(updatedProductDTO.getPrice());
            product.setQuantity(updatedProductDTO.getQuantity());
            product.setDiscount(updatedProductDTO.getDiscount());
            log.info(LogMessages.UPDATED_PRODUCT + "{}", product);
            return productMapper.toDTO(productRepository.save(product));
        }).orElseThrow(() -> {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", updatedProductDTO.getId());
            return new ProductNotFoundException(LogMessages.PRODUCT_NOT_FOUND_BY_ID + updatedProductDTO.getId());
        });
    }

    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void deleteProduct(UUID id) {
        log.info(LogMessages.DELETING_PRODUCT + "{}", id);
        if (!productRepository.existsById(id)) {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
            throw new ProductNotFoundException(LogMessages.PRODUCT_NOT_FOUND_BY_ID + id);
        }
        productRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "products", key = "#category", unless = "#result == null")
    public List<ProductDTO> getProductsByCategory(Category category) {
        log.info(LogMessages.FETCHING_PRODUCTS_BY_CATEGORY + "{}", category);
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CachePut(cacheNames = "products", key = "#id")
    public ProductDTO changePrice(UUID id, Double amount) {
        log.info(LogMessages.CHANGING_PRICE + "{}", id);
        return productRepository.findById(id).map(product -> {
            product.setPrice(amount);
            log.info(LogMessages.CHANGING_PRICE + "{}", id);
            return productMapper.toDTO(productRepository.save(product));
        }).orElseThrow(() -> {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
            return new ProductNotFoundException(LogMessages.PRODUCT_NOT_FOUND_BY_ID + id);
        });
    }

    @Transactional
    @CachePut(cacheNames = "products", key = "#id")
    public ProductDTO increaseQuantity(UUID id, int amount) {
        log.info(LogMessages.CHANGING_QUANTITY + "{}", id);
        return productRepository.findById(id).map(product -> {
            product.setQuantity(product.getQuantity() + amount);
            log.info(LogMessages.CHANGING_QUANTITY + "{}", id);
            return productMapper.toDTO(productRepository.save(product));
        }).orElseThrow(() -> {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
            return new ProductNotFoundException(LogMessages.PRODUCT_NOT_FOUND_BY_ID + id);
        });
    }

    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
