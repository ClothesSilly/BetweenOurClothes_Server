package com.betweenourclothes.domain.members.repository;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersLikeStoresPost;
import com.betweenourclothes.domain.stores.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersLikeStoresPostRepository extends JpaRepository<MembersLikeStoresPost, Long> {
    Optional<MembersLikeStoresPost> findByUserAndStore(Members member, Stores post);
}
