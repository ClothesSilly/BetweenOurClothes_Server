package com.betweenourclothes.domain.members;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {


    Optional<Members> findByEmail(String email);
    Optional<Members> findByNickname(String nickname);

}
