package com.example.demo.service;

import com.example.demo.model.DTO.ProductDTO;
import com.example.demo.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing products.
 */
public interface ProductService {

    /**
     * Retrieves all products.
     *
     * @return a list of all products
     */
    List<Product> getAll();

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product
     * @return an {@link Optional} containing the product, or empty if not found
     */
    Optional<Product> getById(Long id);

    /**
     * Creates a new product.
     *
     * @param productDTO the product to create
     * @return the created product
     */
    Product create(ProductDTO productDTO);

    /**
     * Updates an existing product by its ID.
     *
     * @param id      the ID of the product to update
     * @param productDTO the updated product details
     * @return an {@link Optional} containing the updated product, or empty if not found
     */
    Optional<Product> update(Long id, ProductDTO productDTO);

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     */
    void delete(Long id);
}