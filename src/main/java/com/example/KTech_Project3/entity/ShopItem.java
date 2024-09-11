package com.example.KTech_Project3.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
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
    @Setter
    @Column(nullable = false)
    @NotBlank
    private String name;

    // 상품 설명
    @Setter
    @Column(nullable = false)
    @NotBlank
    private String description;

    // 상품 가격
    @Setter
    @Column(nullable = false)
    @Min(0)
    private Integer price;


    // 상품 재고
    @Setter
    @Column(nullable = false)
    @Min(0)
    private Integer stock;


    @Setter
    private String shopItemImg;


    @Setter
    @ManyToOne
    @JoinColumn(name = "shop_id") // shop id
    private Shop shop;

    // 상품을 주문한 주문 목록
    @OneToMany(mappedBy = "shopItem")
    private List<OrderShopItem> orderShopItems = new ArrayList<>();
}