package com.betweenourclothes.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, String> {
    Optional<Email> findByEmail(String email);
}
