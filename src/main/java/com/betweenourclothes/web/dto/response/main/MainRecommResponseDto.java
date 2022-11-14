package com.betweenourclothes.web.dto.response.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainRecommResponseDto {
    private Long id;
    private byte[] image;
    private int likes_cnt;
    private int comments_cnt;
}
