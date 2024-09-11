package com.example.KTech_Project3.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    @NotBlank
    private String title; // 제목

    @Setter
    @Column(nullable = false)
    @NotBlank
    private String description; // 설명

    @Setter
    @Column(nullable = false)
    @Min(0)
    private Integer minimumPrice; // 최소 가격

    @Setter
    private String status; // 최초 등록시 상태는 판매중, 구매 제안 수락 후 판매 완료

    @Setter
    private String itemImg;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id") // 작성자의 ID를 저장하는 컬럼
    private UserEntity user;

    // 반드시 등록 필요는 없음 (지금 구현 x)
    // private String image; // 대표 이미지

    // 다대일 관계 설정
    @Setter
    @OneToMany(mappedBy = "item")
    private List<Offer> offers;


}
