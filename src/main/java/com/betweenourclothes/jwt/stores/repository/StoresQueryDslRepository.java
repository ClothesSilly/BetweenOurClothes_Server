package com.betweenourclothes.jwt.stores.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoresQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;



}
