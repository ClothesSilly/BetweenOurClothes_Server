package com.betweenourclothes.service.closets;

import com.betweenourclothes.domain.closets.ClosetsRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthTokenException;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.jwt.JwtStatus;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClosetsServiceImpl implements ClosetsService{

    private final MembersRepository membersRepository;
    private final ClosetsRepository closetsRepository;
    @Transactional
    @Override
    public void post(ClosetsPostRequestDto requestDto) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail()).orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));
        closetsRepository.save(requestDto.toEntity(member));
    }
}
