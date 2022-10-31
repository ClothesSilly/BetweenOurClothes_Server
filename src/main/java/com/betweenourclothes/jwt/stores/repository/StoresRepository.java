package com.betweenourclothes.jwt.stores.repository;

import com.betweenourclothes.jwt.stores.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoresRepository extends JpaRepository<Stores, Long> {
}
