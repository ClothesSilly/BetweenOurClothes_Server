package com.betweenourclothes.service.stores;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import com.betweenourclothes.web.dto.request.StoresSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import com.betweenourclothes.web.dto.response.StoresImagesResponseDto;
import com.betweenourclothes.web.dto.response.StoresThumbnailsResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoresService {

    /*** 게시글 등록 ***/
    Long post(StoresPostRequestDto requestDto, List<MultipartFile> imgs);

    /*** 게시글 수정 ***/
    void update(Long id, StoresPostRequestDto requestDto, List<MultipartFile> imgs);

    /*** 게시글 삭제 ***/
    void delete(Long id);

    /*** 게시글 조회 by post id ***/
    StoresImagesResponseDto findPostById(Long id);

    /*** 게시글 필터링 ***/
    StoresThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, StoresSearchCategoryAllRequestDto req);
}
