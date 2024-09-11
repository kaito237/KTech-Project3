package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.Shop;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
    private Long id;

    // 쇼핑몰 이름
    private String name;

    // 쇼핑몰 소개
    private String introduction;

    // 소핑몰 분류 (최소 5개 : 의류, 뷰티, 스포츠 용품, 식품, 도서 )
    private String category;

    // 쇼핑몰 상태
    private String status; // 준비중, 개설 신청, 오픈, 폐쇄

    // 폐쇄 요청 상태: 신청, 수락
    private String closureRequest;

    // 폐쇄 요청 사유
    private String closureReason;

    // 개설 허가, 불허가 (관리자 담당)
    private String openAcceptRefuse;

    // 관리가자가 입력해줌, 쇼핑몰 주인이 볼 수 있도록
    private String refuseReason;

    public static ShopDto fromEntity(Shop shop) {
        if (shop == null) {
            return null; // 또는 적절한 기본값 반환
        }

        return ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .introduction(shop.getIntroduction())
                .category(shop.getCategory() != null ? shop.getCategory().toString() : null)
                .status(shop.getStatus())
                .closureRequest(shop.getClosureRequest())
                .closureReason(shop.getClosureReason())
                .openAcceptRefuse(shop.getOpenAcceptRefuse())
                .refuseReason(shop.getRefuseReason())
                .build();
    }

}