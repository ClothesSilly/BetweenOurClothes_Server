package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.QClothesImage;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import com.querydsl.core.types.Projections;
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
public class ClosetsQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ClothesImage> findClothesImagesByAllOptions(Pageable pageable, Long id, String nameL, String nameS,
                                        String fit, String length,
                                        String material, String color){

        QClosets closets = QClosets.closets;
        QClothesImage clothesImage = QClothesImage.clothesImage;

        List<Closets> posts = queryFactory.select(closets)
                .from(closets)
                .distinct()
                .leftJoin(closets.images, clothesImage)
                .fetchJoin()
                .where(closets.author.id.eq(id),
                        eqNameL(nameL),
                        eqNameS(nameS),
                        eqFit(fit),
                        eqLength(length),
                        eqMaterial(material),
                        eqColor(color))
                .orderBy(closets.createdDate.desc())
                .fetch();

        List<ClothesImage> content = new ArrayList<>();
        for(Closets post: posts){
            content.add(post.getImages().get(0));
        }

        return new PageImpl<ClothesImage>(content, pageable, content.size());
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
