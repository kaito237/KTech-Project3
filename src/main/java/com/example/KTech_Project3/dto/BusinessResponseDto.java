package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.UserEntity;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResponseDto {
    private Long businessId;
    private String name;
    private String authorities;

    public static BusinessResponseDto fromEntity(UserEntity entity) {
        return BusinessResponseDto.builder()
                .businessId(entity.getId())
                .name(entity.getName())
                .authorities(entity.getBusinessStatus())
                .build();
    }

}
