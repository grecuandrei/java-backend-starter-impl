package com.store.application.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "products")
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    @Id
    @JsonProperty(value = "Id")
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID", strategy = GenerationType.UUID)
    @UuidGenerator
    private UUID id;

    @JsonProperty(value = "Name")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @JsonProperty(value = "Description")
    @Column(name = "description")
    private String description;

    @JsonProperty(value = "Category")
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @JsonProperty(value = "Price")
    @Column(name = "price")
    private Double price = 0.0;

    @JsonProperty(value = "Quantity")
    @Column(name = "quantity")
    private int quantity = 0;

    @JsonProperty(value = "Discount")
    @Column(name = "discount")
    private Double discount = 0.0;
}
