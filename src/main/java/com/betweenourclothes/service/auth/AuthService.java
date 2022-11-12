package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.request.auth.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.auth.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.response.auth.AuthSignInResponseDto;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    void signUp(AuthSignUpRequestDto requestDto, String imgPath) throws UnsupportedEncodingException;
    void sendMail(String email) throws UnsupportedEncodingException;

    AuthSignInResponseDto login(AuthSignInRequestDto requestDto);

    AuthTokenResponseDto issueToken(HttpServletRequest request);
    void checkAuthCode(AuthEmailRequestDto receiver);
}
