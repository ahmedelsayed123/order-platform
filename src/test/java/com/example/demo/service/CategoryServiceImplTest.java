package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.DTO.CategoryDTO;
import com.example.demo.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<Category> categories = List.of(new Category(), new Category());
        when(categoryRepo.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAll();

        assertEquals(2, result.size());
        verify(categoryRepo, times(1)).findAll();
    }

    @Test
    void testGetById() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(categoryRepo, times(1)).findById(1L);
    }

    @Test
    void testCreate() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setParentCategory(1L);

        Category parentCategory = new Category();
        parentCategory.setId(1L);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(parentCategory));

        Category category = new Category();
        category.setName("Test Category");
        when(categoryRepo.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.create(categoryDTO);

        assertEquals("Test Category", result.getName());
        verify(categoryRepo, times(1)).findById(1L);
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdate() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Category");
        categoryDTO.setParentCategory(1L);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Category");

        Category parentCategory = new Category();
        parentCategory.setId(1L);

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepo.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Category> result = categoryService.update(1L, categoryDTO);

        assertTrue(result.isPresent());
        assertEquals("Updated Category", result.get().getName());
        verify(categoryRepo, times(2)).findById(1L);
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    void testDelete() {
        categoryService.delete(1L);

        verify(categoryRepo, times(1)).deleteById(1L);
    }

    @Test
    void testGetParentCategory() {
        Category parentCategory = new Category();
        parentCategory.setId(1L);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(parentCategory));

        Category result = categoryService.getParentCategory(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepo, times(1)).findById(1L);
    }

    @Test
    void testGetParentCategoryReturnsNull() {
        Category result = categoryService.getParentCategory(null);

        assertNull(result);
        verify(categoryRepo, times(0)).findById(anyLong());
    }
}