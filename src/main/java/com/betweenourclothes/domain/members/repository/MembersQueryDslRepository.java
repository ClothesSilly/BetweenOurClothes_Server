package com.betweenourclothes.domain.members.repository;

import com.betweenourclothes.domain.clothes.QClothesImage;
import com.betweenourclothes.domain.members.QMembersLikeStoresPost;
import com.betweenourclothes.domain.stores.QStores;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.web.dto.StoresImageTmpDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MembersQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<StoresThumbnailsResponseDto> findByUser(Pageable pageable, Long id){

        QMembersLikeStoresPost likes = QMembersLikeStoresPost.membersLikeStoresPost;
        QStores stores = QStores.stores;
        QClothesImage clothesImage = QClothesImage.clothesImage;


        List<StoresImageTmpDto> tmp_content = queryFactory.select(Projections.constructor(StoresImageTmpDto.class,
                        clothesImage.as("image"), stores.title.as("title"), stores.id.as("id"),
                        stores.modifiedDate.as("modified_date"), stores.price.as("price"),
                        stores.content.as("content"), stores.salesInfo_status.transport.as("transport")
                ))
                .from(stores)
                .join(stores.images, clothesImage)
                .where(stores.id.in(
                            JPAExpressions
                                    .select(likes.store.id)
                                    .from(likes)
                                    .where(likes.user.id.eq(id))),
                        clothesImage.in(
                                JPAExpressions
                                        .select(clothesImage)
                                        .from(clothesImage)
                                        .where(clothesImage.closets_post.isNull())
                                        .groupBy(clothesImage.stores_post).orderBy(clothesImage.stores_post.createdDate.asc()).limit(1)))
                .orderBy(stores.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch().stream().filter(distinctByKey(StoresImageTmpDto::getId))
                .collect(Collectors.toList());

        List<StoresThumbnailsResponseDto> content = new ArrayList<>();
        for(StoresImageTmpDto dto : tmp_content){
            content.add(StoresThumbnailsResponseDto.builder().image(dto.getImage())
                    .id(dto.getId()).title(dto.getTitle()).modified_date(dto.getModified_date())
                    .price(dto.getPrice()).content(dto.getContent()).transport(dto.getTransport())
                    .build());
        }

        return new PageImpl<>(content, pageable, content.size());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private BooleanExpression eqNameL(String nameL){
        if(StringUtils.isEmpty(nameL)){
            return null;
        }
        return QStores.stores.clothesInfo.categoryL.eq(nameL);
    }
}
