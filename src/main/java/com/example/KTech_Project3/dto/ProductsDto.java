package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.Products;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDto {
    private String name;
    private String image;
    private String description;
    private String price;
    private Integer stock;

    public static ProductsDto fromEntity(Products entity) {
        return ProductsDto.builder()
                .name(entity.getName())
                .image(entity.getImage())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }

}
