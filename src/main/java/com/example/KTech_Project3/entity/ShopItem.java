package com.example.KTech_Project3.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ShopItem {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    // 필수 정보
    // 상품 이름
    @Column(nullable = false, name = "name", length = 255)
    @NotBlank// null // "      "
    private String name;

    // 상품 설명
    @Column(nullable = false)
    @NotBlank
    private String description;

    // 상품 가격
    @Column(nullable = false)
    @Min(0)
    private Integer price;


    // 상품 재고
    @Column(nullable = false)
    @Min(0)
    private Integer stock;


    private String shopItemImg;


    @ManyToOne
    @JoinColumn(name = "shop_id") // bieu dien cho khoa ngoai
    private Shop shop;// khoa thi se la object

    // 상품을 주문한 주문 목록
    @OneToMany(mappedBy = "shopItem")
    private List<OrderShopItem> orderShopItems = new ArrayList<>();
}