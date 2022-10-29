package com.betweenourclothes.domain.members.repository;

import com.betweenourclothes.domain.members.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {


    Optional<Members> findByEmail(String email);
    Optional<Members> findByNickname(String nickname);

}
