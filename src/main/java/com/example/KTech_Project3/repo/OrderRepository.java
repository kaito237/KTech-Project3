package com.example.KTech_Project3.repo;

import com.example.KTech_Project3.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOfferName(String name);
}
