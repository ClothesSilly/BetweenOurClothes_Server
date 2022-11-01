package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.clothes.ClothesInfo;
import com.betweenourclothes.domain.stores.SalesInfoClothes;
import com.betweenourclothes.domain.stores.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalesInfoClothesRepository extends JpaRepository<SalesInfoClothes, Long> {
    @Query("select c from SalesInfoClothes c where c.clothes_brand = :brand and c.clothes_gender = :gender" +
            " and c.clothes_size = :size and c.clothes_color = :color")
    Optional<SalesInfoClothes> findByAll(@Param("brand")String clothes_brand, @Param("gender")String clothes_gender,
                                         @Param("size")String clothes_size, @Param("color")String clothes_color);
}
