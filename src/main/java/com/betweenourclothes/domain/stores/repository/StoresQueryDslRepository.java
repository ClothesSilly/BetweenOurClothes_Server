package com.betweenourclothes.domain.stores.repository;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.QClosets;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.QClothesImage;
import com.betweenourclothes.domain.stores.QStores;
import com.betweenourclothes.domain.stores.Stores;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoresQueryDslRepository {

    private final JPAQueryFactory queryFactory;


    /*public Page<ClothesImage> findClothesImagesByKeyword(Pageable pageable, Long id, String keyword){

    }*/

    public Page<ClothesImage> findClothesImagesByAllOptions(Pageable pageable, Long id, String nameL, String nameS,
                                                            String fit, String length,
                                                            String material, String color){

        QStores stores = QStores.stores;
        QClothesImage clothesImage = QClothesImage.clothesImage;

        List<Stores> posts = queryFactory.select(stores)
                .from(stores)
                .distinct()
                .leftJoin(stores.images, clothesImage)
                .fetchJoin()
                .where(stores.author.id.eq(id),
                        eqNameL(nameL),
                        eqNameS(nameS),
                        eqFit(fit),
                        eqLength(length),
                        eqMaterial(material),
                        eqColor(color))
                .orderBy(stores.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ClothesImage> content = new ArrayList<>();
        for(Stores post: posts){
            content.add(post.getImages().get(0));
        }

        return new PageImpl<ClothesImage>(content, pageable, content.size());
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
