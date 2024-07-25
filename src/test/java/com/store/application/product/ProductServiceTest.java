package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts() {
        Product product = new Product();
        product.setName("Test Product");

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Test Product", products.getFirst().getName());
    }

    @Test
    void getProductById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.getProductById(id);

        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
    }

    @Test
    void getProductByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(id);

        assertFalse(foundProduct.isPresent());
    }

    @Test
    void createProduct() {
        Product product = new Product();
        product.setName("Test Product");

        when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(product);

        assertEquals("Test Product", createdProduct.getName());
    }

    @Test
    void createProductAlreadyExists() {
        Product product = new Product();
        product.setName("Test Product");

        when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(product));
    }

    @Test
    void updateProduct() {
        UUID id = UUID.randomUUID();
        Product existingProduct = new Product();
        existingProduct.setId(id);
        existingProduct.setName("Existing Product");

        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(id, updatedProduct);

        assertEquals("Updated Product", result.getName());
    }

    @Test
    void updateProductNotFound() {
        UUID id = UUID.randomUUID();
        Product updatedProduct = new Product();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(id, updatedProduct));
    }

    @Test
    void deleteProduct() {
        UUID id = UUID.randomUUID();

        when(productRepository.existsById(id)).thenReturn(true);
        doNothing().when(productRepository).deleteById(id);

        assertDoesNotThrow(() -> productService.deleteProduct(id));

        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteProductNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
    }

    @Test
    void getProductsByCategory() {
        Category category = Category.FRUITS;
        Product product = new Product();
        product.setCategory(category);
        product.setName("Test Product");

        when(productRepository.findByCategory(category)).thenReturn(List.of(product));

        List<Product> products = productService.getProductsByCategory(category);

        assertEquals(1, products.size());
        assertEquals("Test Product", products.getFirst().getName());
    }

    @Test
    void increaseQuantity() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");
        product.setQuantity(10);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.increaseQuantity(id, 5);

        assertEquals(15, updatedProduct.getQuantity());
    }

    @Test
    void increaseQuantityNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.increaseQuantity(id, 5));
    }
}