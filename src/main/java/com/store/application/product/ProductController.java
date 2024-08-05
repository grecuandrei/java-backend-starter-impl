package com.store.application.product;

import com.store.application.exceptions.ProductAlreadyExistsException;
import com.store.application.exceptions.ProductNotFoundException;
import com.store.application.utils.LogMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@RequestParam("page") int pageIndex,
                                                           @RequestParam("size") int pageSize) {
        log.info(LogMessages.FETCHING_ALL_PRODUCTS);
        Page<ProductDTO> products = productService.getAllProducts(PageRequest.of(pageIndex, pageSize));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Fetching product with id", tags = { "Product", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@Parameter(description = "Product id to get data for", required = true) @PathVariable UUID id) {
        log.info(LogMessages.FETCHING_PRODUCT + "{}", id);
        Optional<ProductDTO> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> {
                    log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
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
    public ResponseEntity<ProductDTO> createProduct(@Parameter(description = "Product data to create", required = true) @RequestBody ProductDTO productDTO) {
        log.info(LogMessages.CREATING_PRODUCT + "{}", productDTO.getName());
        try {
            ProductDTO createdProduct = productService.createProduct(productDTO);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (ProductAlreadyExistsException e) {
            log.error(LogMessages.PRODUCT_ALREADY_EXISTS + "{}", productDTO.getName());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error(LogMessages.ERROR_CREATING_PRODUCT + "{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Updating product with id", tags = { "Product", "put" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated product"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product with the same name already exists")
    })
    @PutMapping
    public ResponseEntity<ProductDTO> updateProduct(@Parameter(description = "Product with updated data", required = true) @RequestBody ProductDTO updatedProductDTO) {
        log.info(LogMessages.UPDATING_PRODUCT + "{}", updatedProductDTO.getId());
        try {
            ProductDTO product = productService.updateProduct(updatedProductDTO);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (ProductAlreadyExistsException e) {
            log.error(LogMessages.PRODUCT_ALREADY_EXISTS + "{}", updatedProductDTO.getName());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ProductNotFoundException e) {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", updatedProductDTO.getId());
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
        log.info(LogMessages.DELETING_PRODUCT + "{}", id);
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ProductNotFoundException e) {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Fetching all products by category", tags = { "Product", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched products by category")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@Parameter(description = "Get products by category", required = true) @PathVariable Category category) {
        log.info(LogMessages.FETCHING_PRODUCTS_BY_CATEGORY + "{}", category);
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Changing price to product with id", tags = { "Product", "patch" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed product price"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/changePrice")
    public ResponseEntity<ProductDTO> changePrice(
            @Parameter(description = "Product id to change the price to", required = true) @PathVariable UUID id,
            @Parameter(description = "The price", required = true) @RequestParam Double amount) {
        log.info(LogMessages.CHANGING_PRICE + "{}", id);
        try {
            ProductDTO product = productService.changePrice(id, amount);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Changing quantity to product with id", tags = { "Product", "patch" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed product quantity"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/increaseQuantity")
    public ResponseEntity<ProductDTO> increaseQuantity(
            @Parameter(description = "Product id to change the quantity to", required = true) @PathVariable UUID id,
            @Parameter(description = "The quantity", required = true) @RequestParam int amount) {
        log.info(LogMessages.CHANGING_QUANTITY + "{}", id);
        try {
            ProductDTO product = productService.increaseQuantity(id, amount);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            log.error(LogMessages.PRODUCT_NOT_FOUND_BY_ID + "{}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Fetching available categories", tags = { "Product", "get" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched categories")
    })
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        log.info("Fetching all categories");
        List<String> categories = productService.getCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
