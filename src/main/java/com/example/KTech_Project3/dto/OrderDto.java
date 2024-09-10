package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.Order;
import com.example.KTech_Project3.entity.UserEntity;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private String itemName;
    private String offerName;
    private String offerStatus;

    private UserEntity user;

    public static OrderDto fromEntity(Order entity) {
        return OrderDto.builder()
                .id(entity.getId())
                .itemName(entity.getItemName())
                .offerName(entity.getOfferName())
                .offerStatus(entity.getOfferStatus())
                .build();
    }
}

