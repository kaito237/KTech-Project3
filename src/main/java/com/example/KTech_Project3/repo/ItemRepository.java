package com.example.KTech_Project3.repo;

import com.example.KTech_Project3.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
