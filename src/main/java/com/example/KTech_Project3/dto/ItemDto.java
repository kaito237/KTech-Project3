package com.example.KTech_Project3.dto;


import com.example.KTech_Project3.entity.Item;
import com.example.KTech_Project3.entity.Order;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String sellerName;
    private String title;
    private String description;
    private String titleImage;
    private Integer price;
    private String status;
    private String response;
    private Order order;
    public static ItemDto fromEntity(Item entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .sellerName(entity.getSellerName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .titleImage(entity.getTitleImage())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .response(entity.getResponse())
                .build();
    }

}
