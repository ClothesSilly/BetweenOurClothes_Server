package com.betweenourclothes.web.dto.response.closets;

import com.betweenourclothes.domain.clothes.ClothesImage;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClosetsThumbnailsResponseDto {
    @ApiParam(value="썸네일 이미지 바이트 배열 (300x300)")
    private byte[] images;

    @ApiParam(value="게시글 id 배열")
    private Long id;

    @Builder
    public ClosetsThumbnailsResponseDto(ClothesImage image, Long id){
        this.images = image.toByte(300, 300);
        this.id = id;
    }
}

