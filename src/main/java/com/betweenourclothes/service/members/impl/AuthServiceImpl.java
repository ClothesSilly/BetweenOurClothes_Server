package com.betweenourclothes.service.members.impl;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.exception.DuplicatedDataException;
import com.betweenourclothes.service.members.AuthService;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
    private final MembersRepository membersRepository;

    @Transactional
    @Override
    public Long signUp(AuthSignUpRequestDto requestDto) throws Exception {

        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(membersRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_NICKNAME);
        }

        return membersRepository.save(requestDto.toEntity()).getId();
    }
}
