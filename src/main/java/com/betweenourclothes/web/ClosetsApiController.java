package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import com.betweenourclothes.service.closets.ClosetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/closets")
public class ClosetsApiController {
    private final ClosetsService closetsService;

    @PostMapping("/post")
    public ResponseEntity<String> post(@RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        closetsService.post(requestDto, imgs);
        return new ResponseEntity<>("등록 완료", HttpStatus.OK);
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest req){
        System.out.println("test 성공");
        return new ResponseEntity<>("테스트 성공", HttpStatus.OK);
    }
}
