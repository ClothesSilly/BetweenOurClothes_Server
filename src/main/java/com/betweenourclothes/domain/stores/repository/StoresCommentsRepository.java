package com.betweenourclothes.domain.stores.repository;


import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.StoresComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoresCommentsRepository extends JpaRepository<StoresComments,Long> {
}
