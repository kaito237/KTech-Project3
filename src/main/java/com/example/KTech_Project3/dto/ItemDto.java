package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.Item;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @Min(0)
    private Integer minimumPrice;

    private String status;
    private String itemImg;

    public static ItemDto fromEntity (Item entity){
        return ItemDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .minimumPrice(entity.getMinimumPrice())
                .itemImg(entity.getItemImg())
                .status(entity.getStatus())
                .build();
    }
}