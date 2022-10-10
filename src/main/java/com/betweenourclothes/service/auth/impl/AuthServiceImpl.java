package com.betweenourclothes.service.auth.impl;

import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.exception.DuplicatedDataException;
import com.betweenourclothes.service.auth.AuthService;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
    private final MembersRepository membersRepository;

    @Transactional
    public Long signUp(AuthSignUpRequestDto requestDto){

        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(membersRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_NICKNAME);
        }

        return membersRepository.save(requestDto.toEntity()).getId();
    }
}
