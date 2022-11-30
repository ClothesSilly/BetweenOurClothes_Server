package com.betweenourclothes.web;

import com.betweenourclothes.service.main.MainService;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/main")
public class MainApiController {

    private final MainService mainService;

    /*** 메인 배너
     * 1. 조회
     * ***/
    @GetMapping("/banner")
    @ApiOperation(value="배너 가져오기")
    public ResponseEntity<List<MainBannerResponseDto>> get_banner(){
        List<MainBannerResponseDto> responseDto = mainService.get_banner();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /*** 메인 추천
     * 1. 최신 등록 물품
     * 2. 추천 상품: 찜 제일 많은 순
     * 3. 사용자가 올린 제일 최신 옷 추천 항목
     * ***/
    @GetMapping("/recomm/latest-product")
    @ApiOperation(value="최신 등록 물품 10개")
    public ResponseEntity<List<MainRecommResponseDto>> get_latest_products(){
        List<MainRecommResponseDto> responseDto = mainService.get_latest_products();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/recomm/best")
    @ApiOperation(value="찜 제일 많은 순 10개")
    public ResponseEntity<List<MainRecommResponseDto>> get_best_products(){
        List<MainRecommResponseDto> responseDto = mainService.get_best_products();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/recomm/user")
    @ApiOperation(value="사용자가 올린 제일 최신 옷 추천 항목 10개")
    public ResponseEntity<List<MainRecommResponseDto>> get_user_recomm_products(){
        List<MainRecommResponseDto> responseDto = mainService.get_user_recomm_products();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
