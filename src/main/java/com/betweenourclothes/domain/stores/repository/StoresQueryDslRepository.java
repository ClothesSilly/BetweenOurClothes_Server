package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.QClosets;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.QClothesImage;
import com.betweenourclothes.domain.stores.QStores;
import com.betweenourclothes.domain.stores.QStoresComments;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.StoresComments;
import com.betweenourclothes.web.dto.response.stores.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
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
public class StoresQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<StoresThumbnailsResponseDto> findByKeyword(Pageable pageable, String keyword) {

        QStores stores = QStores.stores;
        QClothesImage clothesImage = QClothesImage.clothesImage;

        List<StoresThumbnailsResponseDto> content = queryFactory.select(Projections.constructor(StoresThumbnailsResponseDto.class,
                        clothesImage, stores.title, stores.id, stores.modifiedDate, stores.price, stores.content, stores.salesInfo_status.transport
                ))
                .from(stores)
                .join(stores.images, clothesImage)
                .where(
                        clothesImage.in(
                                JPAExpressions
                                        .select(clothesImage)
                                        .from(clothesImage)
                                        .where(clothesImage.closets_post.isNull())
                                        .groupBy(clothesImage.stores_post).orderBy(clothesImage.stores_post.createdDate.asc()).limit(1)),
                        stores.title.contains(keyword).or(stores.content.contains(keyword))
                )
                .orderBy(stores.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch().stream().filter(distinctByKey(StoresThumbnailsResponseDto::getId))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, content.size());
    }
    public Page<StoresPostCommentsResponseDto> findCommentsByUserAndPost(Pageable pageable, Long mid, Long pid){

        QStoresComments comments = QStoresComments.storesComments;
        QStores stores = QStores.stores;

        List<StoresPostCommentsResponseDto> content = queryFactory.select(
                Projections.constructor(StoresPostCommentsResponseDto.class, comments.content, comments.user.nickname, comments.createdDate))
                .from(comments)
                .where(comments.post.id.eq(pid), comments.user.id.eq(mid))
                .orderBy(comments.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        return new PageImpl<>(content, pageable, content.size());
    }

    public Page<StoresThumbnailsResponseDto> findPostsByAllOptions(Pageable pageable, Long id, String nameL, String nameS,
                                                            String fit, String length,
                                                            String material, String color){

        QStores stores = QStores.stores;
        QClothesImage clothesImage = QClothesImage.clothesImage;

        List<StoresThumbnailsResponseDto> list = queryFactory.select(Projections.constructor(StoresThumbnailsResponseDto.class,
                        clothesImage, stores.title, stores.id, stores.modifiedDate, stores.price, stores.content, stores.salesInfo_status.transport
                        ))
                .from(stores)
                .join(stores.images, clothesImage)
                .where(
                        clothesImage.in(
                                JPAExpressions
                                        .select(clothesImage)
                                        .from(clothesImage)
                                        .where(clothesImage.closets_post.isNull())
                                        .groupBy(clothesImage.stores_post).orderBy(clothesImage.stores_post.createdDate.asc()).limit(1)),
                        stores.author.id.eq(id),
                        eqNameL(nameL),
                        eqNameS(nameS),
                        eqFit(fit),
                        eqLength(length),
                        eqMaterial(material),
                        eqColor(color)
                )
                .orderBy(stores.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<StoresThumbnailsResponseDto> content = list.stream()
                .filter(distinctByKey(StoresThumbnailsResponseDto::getId))
                .collect(Collectors.toList());

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

    private BooleanExpression eqNameS(String nameS){
        if(StringUtils.isEmpty(nameS)){
            return null;
        }
        return QStores.stores.clothesInfo.categoryS.eq(nameS);
    }

    private BooleanExpression eqFit(String fit){
        if(StringUtils.isEmpty(fit)){
            return null;
        }
        return QStores.stores.clothesInfo.fit.eq(fit);
    }

    private BooleanExpression eqLength(String length){
        if(StringUtils.isEmpty(length)){
            return null;
        }
        return QStores.stores.clothesInfo.length.eq(length);
    }

    private BooleanExpression eqMaterial(String material){
        if(StringUtils.isEmpty(material)){
            return null;
        }
        return QStores.stores.materials.name.eq(material);
    }

    private BooleanExpression eqColor(String color){
        if(StringUtils.isEmpty(color)){
            return null;
        }
        return QStores.stores.colors.name.eq(color);
    }

}
