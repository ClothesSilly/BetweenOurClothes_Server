package com.betweenourclothes.domain.clothes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorsRepository extends JpaRepository<Colors, Long> {
    Optional<Colors> findByName(String name);
}
