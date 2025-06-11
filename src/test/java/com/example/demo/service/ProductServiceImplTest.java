package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.DTO.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepo;

    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepo.findAll()).thenReturn(products);

        List<Product> result = productService.getAll();

        assertEquals(2, result.size());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    void testGetById() {
        Product product = new Product();
        product.setId(1L);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(productRepo, times(1)).findById(1L);
    }

    @Test
    void testCreate() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(new BigDecimal("100.0"));
        productDTO.setStock(10);
        productDTO.setCategory(1L);
        productDTO.setImageUrl("test-url");

        Category category = new Category();
        category.setId(1L);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));

        Product product = new Product();
        product.setName("Test Product");
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(productDTO);

        assertEquals("Test Product", result.getName());
        verify(categoryRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdate() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setPrice((new BigDecimal("200.0")));
        productDTO.setStock(20);
        productDTO.setCategory(1L);
        productDTO.setImageUrl("updated-url");

        Product existingProduct = new Product();
        existingProduct.setId(1L);

        Category category = new Category();
        category.setId(1L);
        when(productRepo.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenReturn(existingProduct);

        Optional<Product> result = productService.update(1L, productDTO);

        assertTrue(result.isPresent());
        assertEquals("Updated Product", result.get().getName());
        verify(productRepo, times(1)).findById(1L);
        verify(categoryRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void testDelete() {
        productService.delete(1L);

        verify(productRepo, times(1)).deleteById(1L);
    }

    @Test
    void testGetCategoryIfExists() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));

        Category result = productService.getCategoryIfExists(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepo, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryIfExistsReturnsNull() {
        Category result = productService.getCategoryIfExists(null);

        assertNull(result);
        verify(categoryRepo, times(0)).findById(anyLong());
    }
}