package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.request.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    void signUp(AuthSignUpRequestDto requestDto);
    void sendMail(AuthEmailRequestDto receiver);

    AuthTokenResponseDto login(AuthSignInRequestDto requestDto);

    AuthTokenResponseDto issueToken(HttpServletRequest request);
    void checkAuthCode(AuthEmailRequestDto receiver);
}
