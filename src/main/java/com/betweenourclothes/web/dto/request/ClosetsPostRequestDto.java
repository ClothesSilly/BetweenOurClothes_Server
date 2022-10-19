package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.Style;
import com.betweenourclothes.domain.members.Members;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class ClosetsPostRequestDto {
    private String content;
    private String style;

    @Builder
    public ClosetsPostRequestDto(String content, String style){
        this.content = content;
        this.style = style;
    }


    public Closets toEntity(Members member, Style style, List<ClothesImage> images){
        return Closets.builder().author(member).content(this.content).style(style).imgs(images).build();
    }

}
