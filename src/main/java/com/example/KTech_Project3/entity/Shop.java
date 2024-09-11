package com.example.KTech_Project3.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private Long id;

    // 쇼핑몰 이름
    @Setter
    private String name;

    // 쇼핑몰 소개
    @Setter
    private String introduction;

    // 소핑몰 분류 (최소 5개 : 의류, 뷰티, 스포츠 용품, 식품, 도서 )
    @Setter
    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    // 쇼핑몰 상태
    @Setter
    private String status; // 준비중, 개설 신청, 오픈, 폐쇄

    // 폐쇄 요청 상태: 신청, 수락
    @Setter
    private String closureRequest;

    // 폐쇄 요청 사유
    @Setter
    private String closureReason;

    // 개설 허가, 불허가 (관리자 담당)
    @Setter
    private String openAcceptRefuse;

    // (관리자 담당) 개설 신청 거절 이유 ,쇼핑몰 주인이 볼 수 있도록
    @Setter
    private String refuseReason;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id") // 작성자의 ID를 저장하는 컬럼
    private UserEntity user;

    @Setter
    @OneToMany(mappedBy = "shop")
    private List<ShopItem> shopItemList = new ArrayList<>();
}
