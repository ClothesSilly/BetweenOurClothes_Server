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
    public byte[] image;

    @ApiParam(value="게시글 id 배열")
    public Long id;

    @Builder
    public ClosetsThumbnailsResponseDto(byte[] image, Long id){
        this.image = image;
        this.id = id;
    }
}

