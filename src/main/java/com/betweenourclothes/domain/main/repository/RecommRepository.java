package com.betweenourclothes.domain.main.repository;

import com.betweenourclothes.domain.main.Recomm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecommRepository extends JpaRepository<Recomm, Long> {

    @Query(value =
            "select r " +
                    "from Recomm r"
                    + " where r.stores.id = :sid and r.closets.id = :cid"
    )
    Optional<Recomm> findByStoreIdAndClosetId(@Param("sid") Long sid, @Param("cid") Long cid);
}
