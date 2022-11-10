package com.betweenourclothes.domain.closets.repository;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.QClosets;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.QClothesImage;
import com.betweenourclothes.web.dto.response.closets.ClosetsThumbnailsResponseDto;
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

import static com.betweenourclothes.domain.stores.QStores.stores;

@Repository
@RequiredArgsConstructor
public class ClosetsQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ClosetsThumbnailsResponseDto> findPostsByAllOptions(Pageable pageable, Long id, String nameL, String nameS,
                                                                    String fit, String length,
                                                                    String material, String color){

        QClosets closets = QClosets.closets;
        QClothesImage clothesImage = QClothesImage.clothesImage;

        List<ClosetsThumbnailsResponseDto> content = queryFactory.select(Projections.constructor(ClosetsThumbnailsResponseDto.class,
                        clothesImage.as("image"), closets.id.as("id")
                ))
                .from(closets)
                .join(closets.images, clothesImage)
                .where(
                        clothesImage.in(
                                JPAExpressions
                                        .select(clothesImage)
                                        .from(clothesImage)
                                        .where(clothesImage.stores_post.isNull())
                                        .groupBy(clothesImage.closets_post).orderBy(clothesImage.closets_post.createdDate.asc()).limit(1)),
                        closets.author.id.eq(id),
                        eqNameL(nameL),
                        eqNameS(nameS),
                        eqFit(fit),
                        eqLength(length),
                        eqMaterial(material),
                        eqColor(color)
                )
                .orderBy(closets.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch().stream().filter(distinctByKey(ClosetsThumbnailsResponseDto::getId))
                .collect(Collectors.toList());

        return new PageImpl<ClosetsThumbnailsResponseDto>(content, pageable, content.size());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private BooleanExpression eqNameL(String nameL){
        if(StringUtils.isEmpty(nameL)){
            return null;
        }
        return QClosets.closets.clothesInfo.categoryL.eq(nameL);
    }

    private BooleanExpression eqNameS(String nameS){
        if(StringUtils.isEmpty(nameS)){
            return null;
        }
        return QClosets.closets.clothesInfo.categoryS.eq(nameS);
    }

    private BooleanExpression eqFit(String fit){
        if(StringUtils.isEmpty(fit)){
            return null;
        }
        return QClosets.closets.clothesInfo.fit.eq(fit);
    }

    private BooleanExpression eqLength(String length){
        if(StringUtils.isEmpty(length)){
            return null;
        }
        return QClosets.closets.clothesInfo.length.eq(length);
    }

    private BooleanExpression eqMaterial(String material){
        if(StringUtils.isEmpty(material)){
            return null;
        }
        return QClosets.closets.materials.name.eq(material);
    }

    private BooleanExpression eqColor(String color){
        if(StringUtils.isEmpty(color)){
            return null;
        }
        return QClosets.closets.colors.name.eq(color);
    }


}
