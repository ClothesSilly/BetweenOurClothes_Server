package com.betweenourclothes.web.dto.request.stores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoresPostSearchRequestDto {
    @NotNull
    private String keyword;
}
