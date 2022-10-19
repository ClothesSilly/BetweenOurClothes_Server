package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
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
        Long id = closetsService.post(requestDto, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }

    @PatchMapping("/post/{id}")
    public ResponseEntity<String> update(@PathVariable @RequestPart(name="id") String id,
                                         @RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                         @RequestPart(name="image") List<MultipartFile> imgs){
        closetsService.update(Long.parseLong(id), requestDto, imgs);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        closetsService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }


    @PostMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest req){
        System.out.println("test 성공");
        return new ResponseEntity<>("테스트 성공", HttpStatus.OK);
    }
}
