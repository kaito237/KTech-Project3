package com.example.KTech_Project3.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderShopItem {
    // shop item 거래 테이블
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "shop_item_id")
    private ShopItem shopItem;

    @Setter
    @ManyToOne
    @JoinColumn(name = "shop_id") // shop id
    private Shop shop;

    @Setter
    @Column(nullable = false)
    private Integer amount;

    @Setter
    private Integer totalPrice;

    @Setter
    private String status;

    @ManyToOne // 주문한 사람의 USER ID
    @JoinColumn(name = "user_id")
    private UserEntity user;


    @Setter
    @Column(nullable = false)
    private LocalDateTime dateTime;
}