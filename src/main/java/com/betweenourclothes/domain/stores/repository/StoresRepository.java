package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.stores.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface StoresRepository extends JpaRepository<Stores, Long> {
}
