package com.betweenourclothes.domain.clothes.repository;

import com.betweenourclothes.domain.clothes.Colors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorsRepository extends JpaRepository<Colors, Long> {
    Optional<Colors> findByName(String name);
}
