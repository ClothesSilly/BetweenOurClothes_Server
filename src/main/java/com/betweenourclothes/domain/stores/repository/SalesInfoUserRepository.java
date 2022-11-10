package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.stores.SalesInfoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalesInfoUserRepository extends JpaRepository<SalesInfoUser,Long> {

    @Query("select u from SalesInfoUser u where u.user_size = :size and u.user_weight = :weight" +
            " and u.user_height = :height")
    Optional<SalesInfoUser> findByAll(@Param("size")String user_size, @Param("weight")String user_weight,
                                        @Param("height")String user_height);
}
