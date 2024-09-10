package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.Shop;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {
    private Long id;
    private String shopName;
    private String introduction;
    // 쇼핑몰 오픈 상태
    private String shopStatus;
    // 쇼핑몰 개설 응답
    private String shopResponse;
    // 개설 불가 이유
    private String reason;
    // 폐쇄 이유
    private String deleteReason;

    public boolean condition =
            shopName != null && introduction != null;
    public static ShopDto fromEntity(Shop entity) {
        return ShopDto.builder()
                .id(entity.getId())
                .shopName(entity.getShopName())
                .introduction(entity.getIntroduction())
                .shopStatus(entity.getShopStatus())
                .shopResponse(entity.getShopResponse())
                .reason(entity.getReason())
                .build();
    }

}
