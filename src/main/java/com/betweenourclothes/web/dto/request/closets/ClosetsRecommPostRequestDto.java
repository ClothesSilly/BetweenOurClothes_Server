package com.betweenourclothes.web.dto.request.closets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosetsRecommPostRequestDto {
    private Long clothes_info;
    private String style;
    private String material;
    private String color;
}
