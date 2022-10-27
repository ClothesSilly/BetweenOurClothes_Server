package com.betweenourclothes.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosetsPostSearchCategoryLSRequestDto {
    private String nameL;
    private String nameS;
}
