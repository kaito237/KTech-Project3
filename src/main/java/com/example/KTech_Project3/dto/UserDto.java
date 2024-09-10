package com.example.KTech_Project3.dto;

import com.example.KTech_Project3.entity.UserEntity;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String password;
    @Setter
    private String businessStatus;
    public static UserDto fromEntity(UserEntity entity) {
        return UserDto.builder()
                .businessStatus(entity.getBusinessStatus())
                .build();
    }
}
