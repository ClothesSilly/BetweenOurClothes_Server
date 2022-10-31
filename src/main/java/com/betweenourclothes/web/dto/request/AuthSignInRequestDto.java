package com.betweenourclothes.web.dto.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignInRequestDto {
    @ApiParam(value="이메일", required = true)
    private String email;
    @ApiParam(value="비밀번호", required = true)
    private String password;
}
