package com.betweenourclothes.service.auth;

import com.betweenourclothes.web.dto.request.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    void signUp(AuthSignUpRequestDto requestDto, String imgPath);
    void sendMail(String email);

    AuthTokenResponseDto login(AuthSignInRequestDto requestDto);

    AuthTokenResponseDto issueToken(HttpServletRequest request);
    void checkAuthCode(AuthEmailRequestDto receiver);
}
