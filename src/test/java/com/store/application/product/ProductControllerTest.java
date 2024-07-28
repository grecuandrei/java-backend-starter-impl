package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllProducts() throws Exception {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().getFirst().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductById() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");

        when(productService.getProductById(id)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isOk());

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(productService.getProductById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isNotFound());

        ResponseEntity<Product> response = productController.getProductById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Product\"}"))
                .andExpect(status().isCreated());

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProductConflict() throws Exception {
        Product product = new Product();
        product.setName("Test Product");

        when(productService.createProduct(any(Product.class))).thenThrow(new ProductAlreadyExistsException("Product already exists"));

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Product\"}"))
                .andExpect(status().isConflict());

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Updated Product");

        when(productService.updateProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Product\"}"))
                .andExpect(status().isOk());

        ResponseEntity<Product> response = productController.updateProduct(product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Product", response.getBody().getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProductNotFound() throws Exception {
        Product product = new Product();

        when(productService.updateProduct(any(Product.class))).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(put("/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Product\"}"))
                .andExpect(status().isNotFound());

        ResponseEntity<Product> response = productController.updateProduct(product);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/" + id))
                .andExpect(status().isNoContent());

        ResponseEntity<Void> response = productController.deleteProduct(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProductNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ProductNotFoundException("Product not found")).when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/" + id))
                .andExpect(status().isNotFound());

        ResponseEntity<Void> response = productController.deleteProduct(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductsByCategory() throws Exception {
        Category category = Category.FRUITS;
        Product product = new Product();
        product.setCategory(category);
        product.setName("Test Product");

        when(productService.getProductsByCategory(category)).thenReturn(List.of(product));

        mockMvc.perform(get("/products/category/" + category.name()))
                .andExpect(status().isOk());

        ResponseEntity<List<Product>> response = productController.getProductsByCategory(category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().getFirst().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testIncreaseQuantity() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Test Product");
        product.setQuantity(10);

        when(productService.increaseQuantity(eq(id), anyInt())).thenReturn(product);

        mockMvc.perform(patch("/products/" + id + "/increaseQuantity?amount=5"))
                .andExpect(status().isOk());

        ResponseEntity<Product> response = productController.increaseQuantity(id, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().getQuantity());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testIncreaseQuantityNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(productService.increaseQuantity(eq(id), anyInt())).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(patch("/products/" + id + "/increaseQuantity?amount=5"))
                .andExpect(status().isNotFound());

        ResponseEntity<Product> response = productController.increaseQuantity(id, 5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
