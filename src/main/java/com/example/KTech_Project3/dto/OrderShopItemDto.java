package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.OrderShopItem;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderShopItemDto {
    private Long id;
    private Long shopItemId;
    private Integer amount;
    private Integer totalPrice;
    private String status;
    private Long userId;
    private LocalDateTime dateTime;

    public static OrderShopItemDto fromEntity (OrderShopItem entity) {
        return OrderShopItemDto.builder()
                .id(entity.getId())
                .shopItemId(entity.getShopItem().getId())
                .amount(entity.getAmount())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .userId(entity.getUser().getId())
                .dateTime(entity.getDateTime())
                .build();
    }
}
