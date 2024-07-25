package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    public ProductControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.getAllProducts()).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().getFirst().getName());
    }

    @Test
    public void testGetProductById() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");

        when(productService.getProductById(id)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    public void testGetProductByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(productService.getProductById(id)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateProduct() {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    public void testCreateProductConflict() {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.createProduct(any(Product.class))).thenThrow(new ProductAlreadyExistsException("Product already exists"));

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testUpdateProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setName("Updated Product");

        when(productService.updateProduct(eq(id), any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct(id, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Product", response.getBody().getName());
    }

    @Test
    public void testUpdateProductNotFound() {
        UUID id = UUID.randomUUID();
        Product product = new Product();

        when(productService.updateProduct(eq(id), any(Product.class))).thenThrow(new RuntimeException("Product not found"));

        ResponseEntity<Product> response = productController.updateProduct(id, product);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteProduct() {
        UUID id = UUID.randomUUID();

        doNothing().when(productService).deleteProduct(id);

        ResponseEntity<Void> response = productController.deleteProduct(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteProductNotFound() {
        UUID id = UUID.randomUUID();

        doThrow(new ProductNotFoundException("Product not found")).when(productService).deleteProduct(id);

        ResponseEntity<Void> response = productController.deleteProduct(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetProductsByCategory() {
        Category category = Category.FRUITS;
        Product product = new Product();
        product.setCategory(category);
        product.setName("Test Product");

        when(productService.getProductsByCategory(category)).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productController.getProductsByCategory(category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().getFirst().getName());
    }

    @Test
    public void testIncreaseQuantity() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setName("Test Product");
        product.setQuantity(10);

        when(productService.increaseQuantity(eq(id), anyInt())).thenReturn(product);

        ResponseEntity<Product> response = productController.increaseQuantity(id, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().getQuantity());
    }

    @Test
    public void testIncreaseQuantityNotFound() {
        UUID id = UUID.randomUUID();

        when(productService.increaseQuantity(eq(id), anyInt())).thenThrow(new RuntimeException("Product not found"));

        ResponseEntity<Product> response = productController.increaseQuantity(id, 5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
