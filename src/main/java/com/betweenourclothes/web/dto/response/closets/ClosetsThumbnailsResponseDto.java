package com.betweenourclothes.web.dto.response.closets;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClosetsThumbnailsResponseDto {
    @ApiParam(value="썸네일 이미지 바이트 배열 (300x300)")
    List<byte[]> images;
    @ApiParam(value="게시글 id 배열")
    List<Long> id;
    @ApiParam(value="이미지 개수")
    int length;
}

