package com.betweenourclothes.service.main;

import com.betweenourclothes.web.dto.request.main.MainRecommPostRequestDto;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommResponseDto;

import java.util.List;

public interface MainService {

    /*** 내 옷과 어울리는 옷 추천
     * 1. 등록
     * 2. 수정
     * 3. 조회
     * ***/
    void post_recomm(Long id, MainRecommPostRequestDto requestDto);
    void update_recomm(Long id, MainRecommPostRequestDto requestDto);
    List<MainRecommPostResponseDto> get_recomm(Long id);

    /*** 메인 배너
     * 1. 조회
     * ***/
    List<MainBannerResponseDto> get_banner();

    /*** 메인 추천
     * 1. 최신 등록 물품
     * 2. 추천 상품: 찜 제일 많은 순
     * 3. 사용자가 올린 제일 최신 옷 추천 항목
     * ***/
    List<MainRecommResponseDto> get_latest_products();
    List<MainRecommResponseDto> get_best_products();
    List<MainRecommResponseDto> get_user_recomm_products();

}
