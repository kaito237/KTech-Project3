package com.example.KTech_Project3.repo;

import com.example.KTech_Project3.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByTitle(String title);

    Optional<Item>  findByTitleAndUserId(String title, Long id);
}
