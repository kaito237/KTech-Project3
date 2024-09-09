package com.example.KTech_Project3.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // nguoi mua hang

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product; //san pham da mua

    private int quantity; // so luong mua

    private LocalDateTime orderDate;

    //trang thai ("PENDING", "COMPLETED", "CANCELLED")
    private String status;
}
