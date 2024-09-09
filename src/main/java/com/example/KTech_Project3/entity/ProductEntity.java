package com.example.KTech_Project3.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String name;

    private BigDecimal price;

    private int stock;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity store;
}
