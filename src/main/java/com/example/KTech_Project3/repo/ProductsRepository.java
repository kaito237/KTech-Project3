package com.example.KTech_Project3.repo;

import com.example.KTech_Project3.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Long> {
}
