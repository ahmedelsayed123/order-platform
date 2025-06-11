package com.example.demo.model.DTO;

import com.example.demo.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String name;
    private BigDecimal price;
    private int stock;
    private Long category;
    private String imageUrl;


}