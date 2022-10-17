package com.betweenourclothes.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import com.betweenourclothes.service.closets.ClosetsService;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/closets")
public class ClosetsApiController {
    private final ClosetsService closetsService;

    @PostMapping("/post")
    ResponseEntity<String> post(ClosetsPostRequestDto requestDto){
        closetsService.post(requestDto);
        return new ResponseEntity<>("등록 완료", HttpStatus.OK);
    }
}
