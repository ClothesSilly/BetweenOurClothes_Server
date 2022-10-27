package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.config.domain.closets.Closets;
import com.betweenourclothes.config.domain.clothes.ClothesImage;
import com.betweenourclothes.config.domain.clothes.Style;
import com.betweenourclothes.config.domain.members.Members;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class ClosetsPostRequestDto {
    private String style;

    @Builder
    public ClosetsPostRequestDto(String style){
        this.style = style;
    }


    public Closets toEntity(Members member, Style style, List<ClothesImage> images){
        return Closets.builder().author(member).style(style).imgs(images).build();
    }

}
