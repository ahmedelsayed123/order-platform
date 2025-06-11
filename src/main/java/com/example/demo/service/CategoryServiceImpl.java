package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.DTO.CategoryDTO;
import com.example.demo.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryServiceImpl(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    public Optional<Category> getById(Long id) {
        return categoryRepo.findById(id);
    }

    public Category create(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setParentCategory(getParentCategory(categoryDTO.getParentCategory()));
        return categoryRepo.save(category);
    }

    public Optional<Category> update(Long id, CategoryDTO categoryDTO) {
        return categoryRepo.findById(id).map(existing -> {
            existing.setName(categoryDTO.getName());
            existing.setParentCategory(getParentCategory(categoryDTO.getParentCategory()));
            return categoryRepo.save(existing);
        });
    }

    Category getParentCategory(Long parentCategoryId) {
        if (parentCategoryId != null) {
            return categoryRepo.findById(parentCategoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent category with ID " + parentCategoryId + " not found"));
        }
        return null;
    }
    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }
}
