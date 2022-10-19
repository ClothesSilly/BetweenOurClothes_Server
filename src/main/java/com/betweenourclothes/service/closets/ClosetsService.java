package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClosetsService {

    //POST
    Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);
    // 추천항목 등록

    //PATCH
    void update(Long id, ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);

    //DELETE
    void delete(Long id);


    //GET
    //1) ID 별로 게시글 가져오기
    //2) 사진만 가져오기
    //  - 전체
    //  - 대분류, 소분류
    //  - 스타일
    //3) 추천항목 불러오기
}
