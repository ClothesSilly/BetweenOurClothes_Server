package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.AuthEmailRequestDto;

public interface AuthService {
    public Long signUp(AuthSignUpRequestDto requestDto);
    void sendMail(AuthEmailRequestDto receiver);

    void checkAuthCode(AuthEmailRequestDto receiver);
}
