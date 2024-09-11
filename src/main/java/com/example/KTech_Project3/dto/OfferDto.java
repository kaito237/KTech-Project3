package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.Offer;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    private Long id;

    private Integer offerPrice;
    private String status;
    private Long itemId;

    public static OfferDto fromEntity(Offer entity) {
        return OfferDto.builder()
                .id(entity.getId())
                .offerPrice(entity.getOfferPrice())
                .status(entity.getStatus())
                .itemId(entity.getItem().getId()) // Item ID 설정
                .build();
    }

}


