package com.example.KTech_Project3.repo;



import com.example.KTech_Project3.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByUserId(Long id);

    List<Offer> findByItemId(Long id);

    List<Offer> findByItemIdAndUserId(Long id, Long id1);

    List<Offer> findByItemIdAndIdNot(Long itemId, Long offerId);

    Optional<Offer> findByUserIdAndOfferPrice(Long id, Long price);

    Optional<Offer> findByItemIdAndUserIdAndStatus(Long id, Long userid, String 수락);

    List<Offer> findByItemIdAndUserIdNot(Long id, Long userid);
}
