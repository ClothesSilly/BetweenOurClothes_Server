package com.betweenourclothes.web;

import com.betweenourclothes.service.members.MembersService;
import com.betweenourclothes.web.dto.MembersSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MembersApiController {

    private final MembersService membersService;

    @PostMapping("/api/v1/members")
    public Long save(@RequestBody MembersSaveRequestDto requestDto){
        return membersService.save(requestDto);
    }

}
