package com.betweenourclothes.service.main;

import com.betweenourclothes.web.dto.request.main.MainRecommPostRequestDto;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;

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


}
