package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosetsPostRequestDto {
    private String style;
    private String color;
    private String material;
    private String large_category;
    private String small_category;
    private String fit;
    private String length;


    public Closets toEntity(Members member, Style style, Materials material, Colors color,
                            ClothesInfo clothesInfo, List<ClothesImage> images){
        return Closets.builder()
                .author(member)
                .style(style)
                .materials(material)
                .colors(color)
                .clothesInfo(clothesInfo)
                .imgs(images)
                .build();
    }

}
