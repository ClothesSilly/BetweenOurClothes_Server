package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClosetsService {

    /*** 게시글 등록 ***/
    Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);
    // 추천항목 등록

    /*** 게시글 수정 ***/
    void update(Long id, ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);

    /*** 게시글 삭제 ***/
    void delete(Long id);

    /*** 게시글 조회 ***/
    // 1. post id로 게시글 찾아오기 (full image)
    ClosetsImagesResponseDto findPostById(Long id);

    // 2. 카테고리 필터링 (300x300)
    ClosetsThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, ClosetsSearchCategoryAllRequestDto req);


    //ClosetsThumbnailsResponseDto findImagesByCreatedDateDescDisplay();

}
