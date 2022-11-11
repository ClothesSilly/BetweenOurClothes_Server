package com.betweenourclothes.web.dto;

import com.betweenourclothes.domain.clothes.ClothesImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClosetsImageTmpDto {
    private Long id;
    private ClothesImage image;

    @Builder
    public ClosetsImageTmpDto(ClothesImage image, Long id){
        this.id = id;
        this.image = image;
    }
}
