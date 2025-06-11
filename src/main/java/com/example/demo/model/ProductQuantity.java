package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "product_quantities")
public class ProductQuantity {
    @Id
    @GeneratedValue
    private Long id;

    private Long productId;
    private Integer quantity;
}