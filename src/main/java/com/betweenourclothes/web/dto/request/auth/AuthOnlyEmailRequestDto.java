package com.betweenourclothes.web.dto.request.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor
public class AuthOnlyEmailRequestDto {
    private String email;

    @Builder
    public AuthOnlyEmailRequestDto(String email){
        this.email = email;
    }
}
