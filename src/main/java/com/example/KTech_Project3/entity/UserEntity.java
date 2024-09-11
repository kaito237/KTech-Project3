package com.example.KTech_Project3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@Table(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    // 추가하고 싶은 정보
    @Setter
    private String nickname;
    @Setter
    private String name;
    @Setter
    private Integer age;
    @Setter
    private String email;
    @Setter
    private String phone;

    // 권한
    // test를 위해서 문자열 하나에 ','로 구분해 권한을 묘사
    // ROLE_INACTIVE (비활성 사용자 - 회원 가입만)
    // ROLE_USER (일반 사용자 - 닉네임, 이메일 등 업데이트 한 비활성 사용자)
    // ROLE_BUSINESS (사업자 사용자)
    // ROLE_ADMIN (관리자)
    @Setter
    private String authorities;

    // 사업자 등록번호
    @Setter
    private String businessNumber;

    // 사업자 전환 신청
    @Setter
    private String apply; // 신청 시 : apply , 수락 ; accept , 거절 : refuse

    @Setter
    private String profileImg; // 이미지


    // 중고거래
    @OneToMany(mappedBy = "user")
    private List<Item> items = new ArrayList<>();

    // 중고거래 구매 제안
    @OneToMany(mappedBy = "user")
    private List<Offer> orderItems = new ArrayList<>();

    // 쇼핑몰 소유자
    @OneToMany(mappedBy = "user")
    private List<Shop> ownedShops = new ArrayList<>();
}
