package com.betweenourclothes.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosetsSearchCategoryAllRequestDto {
    private String nameL;
    private String nameS;
    private String fit;
    private String length;
    private String material;
    private String color;
}
