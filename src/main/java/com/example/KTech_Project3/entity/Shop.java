package com.example.KTech_Project3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String shopName;
    private String introduction;
//    private ShopCategory category;
    private String shopStatus;
    private String shopResponse;
    private String reason;
    private String deleteReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<Items> itemsList = new ArrayList<>();


}
