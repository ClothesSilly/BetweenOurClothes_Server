package com.betweenourclothes.web.dto.response;

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
public class ClosetsImagesResponseDto {
    @ApiParam(value="게시글 이미지 바이트 배열")
    List<byte[]> images;
    @ApiParam(value="게시글 id")
    Long id;
}
