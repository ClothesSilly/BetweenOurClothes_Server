package com.betweenourclothes.web.dto.request.auth;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Getter
@NoArgsConstructor
public class AuthSignInRequestDto {
    @ApiParam(value="이메일", required = true)
    private String email;
    @ApiParam(value="비밀번호", required = true)
    private String password;

    @Builder
    public AuthSignInRequestDto(String email, String password) throws UnsupportedEncodingException {
        this.email = URLDecoder.decode(email, "utf-8");
        this.password = password;
    }

}
