package com.betweenourclothes.web;

import com.betweenourclothes.service.members.MembersService;
import com.betweenourclothes.web.dto.MembersRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MembersApiController {

    private final MembersService membersService;

    @PostMapping("/api/v1/members")
    public Long register(@RequestBody MembersRegisterRequestDto requestDto){
        return membersService.register(requestDto);
    }

    @GetMapping("/api/v1/members/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }

}
