package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.*;

public interface AuthService {
    void signUp(AuthSignUpRequestDto requestDto);
    void sendMail(AuthEmailRequestDto receiver);

    AuthTokenResponseDto login(AuthSignInRequestDto requestDto);

    AuthTokenResponseDto issueToken(AuthTokenRequestDto requestDto);
    void checkAuthCode(AuthEmailRequestDto receiver);
}
