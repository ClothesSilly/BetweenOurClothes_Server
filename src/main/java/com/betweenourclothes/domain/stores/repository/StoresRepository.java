package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.stores.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoresRepository extends JpaRepository<Stores, Long> {

}
