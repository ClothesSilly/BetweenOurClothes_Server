package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.QClosets;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.QClothesImage;
import com.betweenourclothes.domain.stores.QStores;
import com.betweenourclothes.domain.stores.QStoresComments;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.StoresComments;
import com.betweenourclothes.web.dto.ClosetsImageTmpDto;
import com.betweenourclothes.web.dto.StoresImageTmpDto;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.jboss.jandex.Main;
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

        List<StoresImageTmpDto> tmp_content = queryFactory.select(Projections.constructor(StoresImageTmpDto.class,
                        clothesImage.as("image"),
                        stores.title.as("title"),
                        stores.id.as("id"),
                        stores.modifiedDate.as("modified_date"),
                        stores.price.as("price"),
                        stores.content.as("content"),
                        stores.salesInfo_status.transport.as("transport")
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
                .fetch()
                .stream()
                .filter(distinctByKey(StoresImageTmpDto::getId))
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
    public Page<StoresPostCommentsResponseDto> findCommentsByUserAndPost(Pageable pageable, Long mid, Long pid){

        QStoresComments comments = QStoresComments.storesComments;
        QStores stores = QStores.stores;

        List<StoresPostCommentsResponseDto> content = queryFactory.select(
                Projections.constructor(StoresPostCommentsResponseDto.class, comments.content.as("comments"), comments.user.nickname.as("nickname"), comments.createdDate.as("createdTime")))
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

        List<StoresImageTmpDto> tmp_content = queryFactory.select(Projections.constructor(StoresImageTmpDto.class,
                        clothesImage.as("image"), stores.title.as("title"), stores.id.as("id"),
                        stores.modifiedDate.as("modified_date"), stores.price.as("price"),
                        stores.content.as("content"), stores.salesInfo_status.transport.as("transport")
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
                .fetch().stream()
                .filter(distinctByKey(StoresImageTmpDto::getId))
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

    public List<ClosetsImageTmpDto> findLatestProducts(){
        QStores stores = QStores.stores;
        QClothesImage clothesImage = QClothesImage.clothesImage;

        List<ClosetsImageTmpDto> contents = queryFactory.select(Projections.constructor(ClosetsImageTmpDto.class,
                clothesImage.as("image"), stores.id.as("id")))
                .from(stores)
                .join(stores.images, clothesImage)
                .orderBy(stores.createdDate.desc())
                .limit(10)
                .fetch().stream()
                .filter(distinctByKey(ClosetsImageTmpDto::getId))
                .collect(Collectors.toList());;

        return contents;
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
