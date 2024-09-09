package com.example.KTech_Project3.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "stores")
public class StoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // ten cua hang

    private String category; // phan loai cua hang

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner; // chu so huu cua hang

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private Set<ProductEntity> products; //danh sach san pham trong cua hang


}
