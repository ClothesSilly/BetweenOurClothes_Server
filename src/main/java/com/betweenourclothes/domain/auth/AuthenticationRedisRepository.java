package com.betweenourclothes.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AuthenticationRedisRepository extends CrudRepository<Authentication, String> {
    Optional<Authentication> findByEmail(String email);
    List<Authentication> findAll();
}
