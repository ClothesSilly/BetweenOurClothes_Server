package com.betweenourclothes.web.dto.request.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Getter

@NoArgsConstructor
public class AuthOnlyEmailRequestDto {
    private String email;

    @Builder
    public AuthOnlyEmailRequestDto(String email) throws UnsupportedEncodingException {
        this.email = URLDecoder.decode(email, "utf-8");;
    }
}
