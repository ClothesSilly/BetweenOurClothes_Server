package com.betweenourclothes.web.dto.response.auth;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenResponseDto {
    @ApiParam(value="인증 타입")
    private String grantType;
    @ApiParam(value="access 토큰")
    private String accessToken;
    @ApiParam(value="refresh 토큰")
    private String refreshToken;
}
