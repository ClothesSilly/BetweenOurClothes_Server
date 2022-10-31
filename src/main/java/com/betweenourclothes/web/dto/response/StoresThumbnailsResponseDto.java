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
public class StoresThumbnailsResponseDto {
    List<byte[]> images;
    List<Long> id;
    int length;
}
