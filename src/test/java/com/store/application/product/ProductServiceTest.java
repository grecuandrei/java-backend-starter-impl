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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void init() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .category(Category.FRUITS)
                .quantity(10)
                .price(10.0)
                .build();

        productDTO = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(String.valueOf(Category.FRUITS))
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        List<ProductDTO> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Test Product", products.getFirst().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductById() {
        UUID id = product.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        Optional<ProductDTO> foundProduct = productService.getProductById(id);

        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Optional<ProductDTO> foundProduct = productService.getProductById(id);

        assertFalse(foundProduct.isPresent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct() {
        when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(product);

        ProductDTO createdProduct = productService.createProduct(productDTO);

        assertEquals("Test Product", createdProduct.getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductAlreadyExists() {
        when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct() {
        UUID id = product.getId();
        productDTO.setName("Test Product Update");
        productDTO.setPrice(15.0);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        ProductDTO updatedProductDTO = productService.updateProduct(productDTO);

        assertEquals("Test Product Update", updatedProductDTO.getName());
        assertEquals(15, updatedProductDTO.getPrice());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductNotFound() {
        UUID id = productDTO.getId();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct() {
        UUID id = product.getId();

        when(productRepository.existsById(id)).thenReturn(true);
        doNothing().when(productRepository).deleteById(id);

        assertDoesNotThrow(() -> productService.deleteProduct(id));

        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductsByCategory() {
        Category category = Category.FRUITS;
        product.setCategory(category);

        when(productRepository.findByCategory(category)).thenReturn(List.of(product));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        List<ProductDTO> products = productService.getProductsByCategory(category);

        assertEquals(1, products.size());
        assertEquals("Test Product", products.getFirst().getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void increaseQuantity() {
        UUID id = product.getId();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return ProductDTO.builder()
                    .id(savedProduct.getId())
                    .quantity(savedProduct.getQuantity())
                    .build();
        });

        ProductDTO updatedProduct = productService.increaseQuantity(id, 5);

        assertEquals(15, updatedProduct.getQuantity());
    }

    @Test
    @WithMockUser(roles = "USER")
    void increaseQuantityNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.increaseQuantity(id, 5));
    }
}