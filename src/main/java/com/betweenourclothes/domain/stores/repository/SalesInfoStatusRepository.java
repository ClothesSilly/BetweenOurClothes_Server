package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.stores.SalesInfoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalesInfoStatusRepository extends JpaRepository<SalesInfoStatus, Long> {
    @Query("select s from SalesInfoStatus s where s.status_tag = :tag and s.status_score = :score" +
            " and s.status_times = :times and s.status_when2buy = :when2buy and s.transport = :transport")
    Optional<SalesInfoStatus> findByAll(@Param("tag")String status_tag, @Param("score")String status_score,
                                         @Param("times")String status_times, @Param("when2buy")String status_when2buy
                                        ,@Param("transport")String transport
    );

}
