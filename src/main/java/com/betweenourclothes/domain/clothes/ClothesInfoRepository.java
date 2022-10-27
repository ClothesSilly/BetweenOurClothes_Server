package com.betweenourclothes.domain.clothes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClothesInfoRepository extends JpaRepository<ClothesInfo, Long> {

    Optional<ClothesInfo> findByCategoryLAndCategorySAndLengthAndFit(String categoryL, String categoryS, String length, String fit);
}
