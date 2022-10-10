package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.AuthSignUpRequestDto;

public interface AuthService{
    public Long signUp(AuthSignUpRequestDto requestDto);
}
