package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import io.swagger.annotations.ApiParam;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosetsPostRequestDto {
    @ApiParam(value="옷 스타일", required = true)
    private String style;
    @ApiParam(value="옷 색", required = true)
    private String color;
    @ApiParam(value="옷 재질", required = true)
    private String material;
    @ApiParam(value="옷 대분류", required = true)
    private String large_category;
    @ApiParam(value="옷 소분류", required = true)
    private String small_category;
    @ApiParam(value="옷 핏", required = true)
    private String fit;
    @ApiParam(value="옷 기장", required = true)
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
