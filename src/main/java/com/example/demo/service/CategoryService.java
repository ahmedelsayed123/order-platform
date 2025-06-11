package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.DTO.CategoryDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing categories.
 */
public interface CategoryService {

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories
     */
    List<Category> getAll();

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category
     * @return an {@link Optional} containing the category, or empty if not found
     */
    Optional<Category> getById(Long id);

    /**
     * Creates a new category.
     *
     * @param category the category to create
     * @return the created category
     */
    Category create(CategoryDTO category);

    /**
     * Updates an existing category by its ID.
     *
     * @param id       the ID of the category to update
     * @param category the updated category details
     * @return an {@link Optional} containing the updated category, or empty if not found
     */
    Optional<Category> update(Long id, CategoryDTO category);

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     */
    void delete(Long id);
}