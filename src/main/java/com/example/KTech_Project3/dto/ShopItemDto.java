package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.ShopItem;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemDto {
    private Long id;

    // 필수 정보
    // 상품 이름
    @Column(nullable = false)
    @NotBlank
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

    public static ShopItemDto fromEntity (ShopItem entity) {
        return ShopItemDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .shopItemImg(entity.getShopItemImg())
                .build();
    }
}