package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


import com.example.demo.model.DTO.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductServiceImpl productServiceImpl;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<Product> products = List.of(new Product(), new Product());
        when(productServiceImpl.getAll()).thenReturn(products);

        List<Product> result = productController.getAll();

        assertEquals(2, result.size());
        verify(productServiceImpl, times(1)).getAll();
    }

    @Test
    void testGetByIdFound() {
        Product product = new Product();
        product.setId(1L);
        when(productServiceImpl.getById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getById(1L);

        assertEquals(ResponseEntity.ok(product), response);
        verify(productServiceImpl, times(1)).getById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(productServiceImpl.getById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.getById(1L);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(productServiceImpl, times(1)).getById(1L);
    }

    @Test
    void testCreate() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");

        Product product = new Product();
        product.setName("Test Product");
        when(productServiceImpl.create(productDTO)).thenReturn(product);

        ResponseEntity<Product> response  = productController.create(productDTO);

        assertNotNull(response);
        verify(productServiceImpl, times(1)).create(productDTO);
    }

    @Test
    void testUpdateFound() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");

        Product product = new Product();
        product.setName("Updated Product");
        when(productServiceImpl.update(1L, productDTO)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.update(1L, productDTO);

        assertEquals(ResponseEntity.ok(product), response);
        verify(productServiceImpl, times(1)).update(1L, productDTO);
    }

    @Test
    void testUpdateNotFound() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");

        when(productServiceImpl.update(1L, productDTO)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.update(1L, productDTO);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(productServiceImpl, times(1)).update(1L, productDTO);
    }

    @Test
    void testDelete() {
        ResponseEntity<Void> response = productController.delete(1L);

        assertEquals(ResponseEntity.noContent().build(), response);
        verify(productServiceImpl, times(1)).delete(1L);
    }
}