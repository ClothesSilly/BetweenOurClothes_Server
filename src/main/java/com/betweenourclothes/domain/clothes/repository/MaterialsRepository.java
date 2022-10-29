package com.betweenourclothes.domain.clothes.repository;

import com.betweenourclothes.domain.clothes.Materials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialsRepository extends JpaRepository<Materials, Long> {
    Optional<Materials> findByName(String name);
}
