package com.betweenourclothes.service.stores;

import com.betweenourclothes.web.dto.request.*;
import com.betweenourclothes.web.dto.response.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.StoresThumbnailsResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoresService {

    /*** 게시글 등록 ***/
    Long post(StoresPostRequestDto postinfo, StoresPostClothesRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs);

    /*** 게시글 수정 ***/
    void update(Long id, StoresPostRequestDto postinfo, StoresPostClothesRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs);

    /*** 게시글 삭제 ***/
    void delete(Long id);

    /*** 게시글 조회 by post id ***/
    StoresPostResponseDto findPostById(Long id);

    /*** 게시글 필터링 ***/
    StoresThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, StoresSearchCategoryAllRequestDto req);
}
