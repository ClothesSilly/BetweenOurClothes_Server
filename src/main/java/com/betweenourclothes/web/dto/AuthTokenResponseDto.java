package com.betweenourclothes.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
