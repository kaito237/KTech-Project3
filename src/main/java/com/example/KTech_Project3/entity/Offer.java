package com.example.KTech_Project3.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Integer offerPrice;

    @Setter
    private String status;


    // 다대일(Many-to-One) 관계 설정
    @Setter
    @ManyToOne
    @JoinColumn(name = "item_id") // 외래키 컬럼명 지정
    private Item item;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id") // 작성자의 ID를 저장하는 컬럼
    private UserEntity user;

}
