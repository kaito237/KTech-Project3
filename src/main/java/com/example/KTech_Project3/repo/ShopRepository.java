package com.example.KTech_Project3.repo;

import com.example.KTech_Project3.entity.Shop;
import org.hibernate.query.KeyedPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findAllByOrderByIdDesc();
    List<Shop> findByShopName(String shopName);



}
