package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.stores.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoresPostRequestDto {
    private String style;
    private String color;
    private String material;
    private String large_category;
    private String small_category;
    private String fit;
    private String length;
    private String title;
    private String content;

    public Stores toEntity(Members member, Style style, Materials material, Colors color,
                           ClothesInfo clothesInfo, List<ClothesImage> images, SalesInfoClothes salesInfoClothes,
                           SalesInfoUser salesInfoUser, SalesInfoStatus salesInfoStatus, Long clothes_length,
                           SalesStatus status){
        return Stores.builder()
                .author(member)
                .style(style)
                .materials(material)
                .colors(color)
                .clothesInfo(clothesInfo)
                .imgs(images)
                .title(title)
                .content(content)
                .salesInfo_clothes(salesInfoClothes)
                .salesInfo_status(salesInfoStatus)
                .salesInfo_user(salesInfoUser)
                .clothes_length(clothes_length)
                .status(status)
                .build();
    }
}
