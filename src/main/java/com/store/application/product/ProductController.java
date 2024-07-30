package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        log.info("Fetching product with id: {}", id);
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("Creating new product: {}", product.getName());
        try {
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (ProductAlreadyExistsException e) {
            log.error("Product already exists with same name: {}", product.getName());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product updatedProduct) {
        log.info("Updating product with id: {}", updatedProduct.getId());
        try {
            Product product = productService.updateProduct(updatedProduct);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", updatedProduct.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        log.info("Deleting product with id: {}", id);
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ProductNotFoundException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Category category) {
        log.info("Fetching all products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        products.forEach(product -> {
            double discountedPrice = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
            product.setPrice(discountedPrice);
        });
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PatchMapping("/{id}/changePrice")
    public ResponseEntity<Product> changePrice(@PathVariable UUID id, @RequestParam Double amount) {
        log.info("Changing price to product with id: {}", id);
        try {
            Product product = productService.changePrice(id, amount);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/increaseQuantity")
    public ResponseEntity<Product> increaseQuantity(@PathVariable UUID id, @RequestParam int amount) {
        log.info("Changing quantity to product with id: {}", id);
        try {
            Product product = productService.increaseQuantity(id, amount);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
