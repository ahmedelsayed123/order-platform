package com.example.demo.service;


import com.example.demo.model.Category;
import com.example.demo.model.DTO.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepo.findById(id);
    }

    public Product create(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(getCategoryIfExists(productDTO.getCategory()));
        product.setImageUrl(productDTO.getImageUrl());
        return productRepo.save(product);
    }

    public Optional<Product> update(Long id, ProductDTO productDTO) {
        return productRepo.findById(id).map(p -> {
            p.setName(productDTO.getName());
            p.setPrice(productDTO.getPrice());
            p.setImageUrl(productDTO.getImageUrl());
            p.setStock(productDTO.getStock());
            p.setCategory(getCategoryIfExists(productDTO.getCategory()));
            return productRepo.save(p);
        });
    }

    Category getCategoryIfExists(Long categoryId) {
        if (categoryId != null) {
            return categoryRepo.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category with ID " + categoryId + " not found"));
        }
        return null;
    }

    public void delete(Long id) {
        productRepo.deleteById(id);
    }
}
