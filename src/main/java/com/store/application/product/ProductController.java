package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "Fetching all products", tags = { "Product", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all products")
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Fetching product with id", tags = { "Product", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@Parameter(description = "Product id to get data for", required = true) @PathVariable UUID id) {
        log.info("Fetching product with id: {}", id);
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @Operation(summary = "Creating new product", tags = { "Product", "post" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created product"),
            @ApiResponse(responseCode = "409", description = "Product already exists"),
            @ApiResponse(responseCode = "400", description = "Error creating product")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@Parameter(description = "Product data to create", required = true) @RequestBody Product product) {
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

    @Operation(summary = "Updating product with id", tags = { "Product", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping
    public ResponseEntity<Product> updateProduct(@Parameter(description = "Product with updated data", required = true) @RequestBody Product updatedProduct) {
        log.info("Updating product with id: {}", updatedProduct.getId());
        try {
            Product product = productService.updateProduct(updatedProduct);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", updatedProduct.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deleting product with id", tags = { "Product", "delete" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "Product id to delete data for", required = true) @PathVariable UUID id) {
        log.info("Deleting product with id: {}", id);
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ProductNotFoundException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Fetching all products by category", tags = { "Product", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched products by category")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@Parameter(description = "Get products by category", required = true) @PathVariable Category category) {
        log.info("Fetching all products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        products.forEach(product -> {
            double discountedPrice = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
            product.setPrice(discountedPrice);
        });
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Changing price to product with id", tags = { "Product", "patch" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed product price"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/changePrice")
    public ResponseEntity<Product> changePrice(
            @Parameter(description = "Product id to change the price to", required = true) @PathVariable UUID id,
            @Parameter(description = "The quantity", required = true) @RequestParam Double amount) {
        log.info("Changing price to product with id: {}", id);
        try {
            Product product = productService.changePrice(id, amount);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Changing quantity to product with id", tags = { "Product", "patch" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed product quantity"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/increaseQuantity")
    public ResponseEntity<Product> increaseQuantity(
            @Parameter(description = "Product id to change the quantity to", required = true) @PathVariable UUID id,
            @Parameter(description = "The quantity", required = true) @RequestParam int amount) {
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
