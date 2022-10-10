package com.betweenourclothes.web;

import com.betweenourclothes.service.members.impl.AuthServiceImpl;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthServiceImpl membersService;


    @PostMapping("/sign-up")
    public Long signUp(@RequestBody @Valid AuthSignUpRequestDto requestDto) throws Exception {
        return membersService.signUp(requestDto);
    }

}
