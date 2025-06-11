package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.DTO.CategoryDTO;
import com.example.demo.service.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryServiceImpl service;

    @InjectMocks
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<Category> categories = List.of(new Category(), new Category());
        when(service.getAll()).thenReturn(categories);

        List<Category> result = controller.getAll();

        assertEquals(2, result.size());
        verify(service, times(1)).getAll();
    }

    @Test
    void testGetByIdFound() {
        Category category = new Category();
        category.setId(1L);
        when(service.getById(1L)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = controller.getById(1L);

        assertEquals(ResponseEntity.ok(category), response);
        verify(service, times(1)).getById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(service.getById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = controller.getById(1L);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(service, times(1)).getById(1L);
    }

    @Test
    void testCreate() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");

        Category category = new Category();
        category.setName("Test Category");
        when(service.create(categoryDTO)).thenReturn(category);

        ResponseEntity<Category> result = controller.create(categoryDTO);

        assertNotNull(result);
        verify(service, times(1)).create(categoryDTO);
    }

    @Test
    void testUpdateFound() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Category");

        Category category = new Category();
        category.setName("Updated Category");
        when(service.update(1L, categoryDTO)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = controller.update(1L, categoryDTO);

        assertEquals(ResponseEntity.ok(category), response);
        verify(service, times(1)).update(1L, categoryDTO);
    }

    @Test
    void testUpdateNotFound() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Updated Category");

        when(service.update(1L, categoryDTO)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = controller.update(1L, categoryDTO);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(service, times(1)).update(1L, categoryDTO);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(ResponseEntity.noContent().build(), response);
        verify(service, times(1)).delete(1L);
    }
}