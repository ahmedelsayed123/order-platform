package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "category")

public class Category {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private Category parentCategory;
}
