package com.betweenourclothes.web.dto.response.main;

import com.betweenourclothes.domain.clothes.ClothesImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MainRecommPostResponseDto {
    private Long id;
    private byte[] image;

    @Builder
    public MainRecommPostResponseDto(Long id, ClothesImage image){
        this.id = id;
        this.image = image.toByte(image.getPath(), 300, 300);
    }
}
