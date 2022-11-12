package com.betweenourclothes.web.dto.response.auth;

import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class AuthSignInResponseDto {
    private byte[] image;
    private String nickname;
    private String grantType;
    private String accessToken;
    private String refreshToken;

    @Builder
    public AuthSignInResponseDto(byte[] image, String nickname, String grantType, String accessToken, String refreshToken){
        this.image = image;
        this.nickname = nickname;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
