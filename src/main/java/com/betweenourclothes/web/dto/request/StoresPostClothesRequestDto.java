package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.stores.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoresPostClothesRequestDto {
    private String style;
    private String color;
    private String material;
    private String large_category;
    private String small_category;
    private String fit;
    private String length;
}
