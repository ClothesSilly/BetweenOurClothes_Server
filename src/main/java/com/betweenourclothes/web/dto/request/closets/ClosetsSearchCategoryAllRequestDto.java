package com.betweenourclothes.web.dto.request.closets;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosetsSearchCategoryAllRequestDto {
    @ApiParam(value="대분류", required = false)
    private String nameL;
    @ApiParam(value="중분류", required = false)
    private String nameS;
    @ApiParam(value="핏", required = false)
    private String fit;
    @ApiParam(value="길이", required = false)
    private String length;
    @ApiParam(value="재질", required = false)
    private String material;
    @ApiParam(value="색", required = false)
    private String color;
}
