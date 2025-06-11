package com.example.demo.repository;

import com.example.demo.model.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductQuantityRepo extends JpaRepository<ProductQuantity, Long> {
}
