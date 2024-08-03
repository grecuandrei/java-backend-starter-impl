package com.store.application.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link Product}
 */
@Data
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {
    private UUID id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Category is required")
    private String category;
    @Min(message = "Price cannot be negative", value = 0)
    private Double price = 0.0;
    @Min(message = "Quantity cannot be negative", value = 0)
    private int quantity = 0;
    @Min(message = "Discount cannot be negative", value = 0)
    private Double discount = 0.0;
}