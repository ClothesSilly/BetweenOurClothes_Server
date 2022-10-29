package com.betweenourclothes.web;

import com.betweenourclothes.service.stores.StoresService;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoresApiController {

    private final StoresService storesService;

    /*** 게시글 등록 ***/
    @PostMapping("/post")
    public ResponseEntity<String> post(@RequestPart(name="data") StoresPostRequestDto requestDto,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = storesService.post(requestDto, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }
    // 글 수정
    // 글 삭제
    // post id로 게시글 가져오기
    // 옷 필터
    // 판매정보 필터
}
