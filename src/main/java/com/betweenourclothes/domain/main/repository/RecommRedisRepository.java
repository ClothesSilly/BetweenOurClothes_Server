package com.betweenourclothes.domain.main.repository;

import com.betweenourclothes.domain.main.RecommRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecommRedisRepository extends CrudRepository<RecommRedis, String> {
    Optional<RecommRedis> findById(String id);
}
