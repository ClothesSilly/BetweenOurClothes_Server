package com.betweenourclothes.web.dto.request.stores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoresPostRequestDto {
    private String title;
    private String content;
    private String price;
}
