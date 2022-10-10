package com.betweenourclothes.service.members;

import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService{
    public Long signUp(AuthSignUpRequestDto requestDto) throws Exception;
}
