package com.example.KTech_Project3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String shopName;
    @Setter
    private String introduction;
//    private ShopCategory category;
    @Setter
    private String shopStatus;
    @Setter
    private String shopResponse;
    @Setter
    private String reason;
    @Setter
    private String deleteReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<Products> productsList = new ArrayList<>();


}
