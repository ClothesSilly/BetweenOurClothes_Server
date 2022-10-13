package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.AuthTokenInfoResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    void signUp(AuthSignUpRequestDto requestDto);
    void sendMail(AuthEmailRequestDto receiver);

    AuthTokenInfoResponseDto login(AuthSignInRequestDto requestDto);
    void checkAuthCode(AuthEmailRequestDto receiver);
}
