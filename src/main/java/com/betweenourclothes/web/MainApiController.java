package com.betweenourclothes.web;

import com.betweenourclothes.service.main.MainService;
import com.betweenourclothes.web.dto.request.main.MainRecommPostRequestDto;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/main")
public class MainApiController {

    private final MainService mainService;

    /*** 내 옷과 어울리는 옷 추천
     * 1. 등록
     * 2. 수정
     * 3. 조회
     * ***/
    @PostMapping("/recomm/{id}")
    @ApiOperation(value="추천 등록", notes = "Models > MainRecommPostRequestDto")
    public ResponseEntity<String> post_recomm(@PathVariable("id") Long id, @RequestBody MainRecommPostRequestDto requestDto){
        mainService.post_recomm(id, requestDto);
        return new ResponseEntity<>("등록 완료", HttpStatus.OK);
    }

    @PutMapping("/recomm/{id}")
    @ApiOperation(value="추천 재업로드", notes = "Models > MainRecommPostRequestDto")
    public ResponseEntity<String> update_recomm(@PathVariable("id") Long id, @RequestBody MainRecommPostRequestDto requestDto){
        mainService.update_recomm(id, requestDto);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }

    @GetMapping("/recomm/{id}")
    @ApiOperation(value="추천항목 가져오기", notes = "게시글 ID Path Variable로 넘기기")
    public ResponseEntity<List<MainRecommPostResponseDto>> get_recomm(@PathVariable("id") Long id){
        List<MainRecommPostResponseDto> responseDto= mainService.get_recomm(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /*** 메인 배너
     * 1. 조회
     * ***/
    @GetMapping("/banner")
    @ApiOperation(value="배너 가져오기")
    public ResponseEntity<List<MainBannerResponseDto>> get_banner(){
        List<MainBannerResponseDto> responseDto = mainService.get_banner();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
