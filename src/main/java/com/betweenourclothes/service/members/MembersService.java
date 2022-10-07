package com.betweenourclothes.service.members;

import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.web.dto.MembersSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MembersService {
    private final MembersRepository membersRepository;

    @Transactional
    public Long save(MembersSaveRequestDto requestDto){
        return membersRepository.save(requestDto.toEntity()).getId();
    }
}
