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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private ProductDTO productDTO;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        productDTO = ProductDTO.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .category(String.valueOf(Category.FRUITS))
                .quantity(10)
                .price(10.0)
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllProducts() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> page = new PageImpl<>(List.of(productDTO), pageable, 1);

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        ResponseEntity<Page<ProductDTO>> response = productController.getAllProducts(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Test Product", response.getBody().getContent().getFirst().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductById() throws Exception {
        when(productService.getProductById(productDTO.getId())).thenReturn(Optional.of(productDTO));

        mockMvc.perform(get("/products/" + productDTO.getId()))
                .andExpect(status().isOk());

        ResponseEntity<ProductDTO> response = productController.getProductById(productDTO.getId());

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

        ResponseEntity<ProductDTO> response = productController.getProductById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Product\"}"))
                .andExpect(status().isCreated());

        ResponseEntity<ProductDTO> response = productController.createProduct(productDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProductConflict() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenThrow(new ProductAlreadyExistsException("Product already exists"));

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Product\"}"))
                .andExpect(status().isConflict());

        ResponseEntity<ProductDTO> response = productController.createProduct(productDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct() throws Exception {
        productDTO.setName("Test Product Update");

        when(productService.updateProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/products")
                        .contentType("application/json")
                        .content("{\"id\":\"" + productDTO.getId() + "\", \"name\":\"Updated Product\"}"))
                .andExpect(status().isOk());

        ResponseEntity<ProductDTO> response = productController.updateProduct(productDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Product Update", response.getBody().getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProductNotFound() throws Exception {
        when(productService.updateProduct(any(ProductDTO.class))).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(put("/products")
                        .contentType("application/json")
                        .content("{\"id\":\"" + productDTO.getId() + "\", \"name\":\"Updated Product\"}"))
                .andExpect(status().isNotFound());

        ResponseEntity<ProductDTO> response = productController.updateProduct(productDTO);

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
        when(productService.getProductsByCategory(Category.FRUITS)).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products/category/" + Category.FRUITS.name()))
                .andExpect(status().isOk());

        ResponseEntity<List<ProductDTO>> response = productController.getProductsByCategory(Category.FRUITS);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().getFirst().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testIncreaseQuantity() throws Exception {
        when(productService.increaseQuantity(eq(productDTO.getId()), anyInt())).thenReturn(productDTO);

        mockMvc.perform(patch("/products/" + productDTO.getId() + "/increaseQuantity?amount=5"))
                .andExpect(status().isOk());

        ResponseEntity<ProductDTO> response = productController.increaseQuantity(productDTO.getId(), 5);

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

        ResponseEntity<ProductDTO> response = productController.increaseQuantity(id, 5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
