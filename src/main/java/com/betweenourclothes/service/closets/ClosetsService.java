package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsPostSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsPostSearchCategoryLSRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import org.springframework.data.domain.Pageable;
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
    ClosetsThumbnailsResponseDto findImagesByCreatedDateDesc(Pageable pageable);

    ClosetsImagesResponseDto findPostById(Long id);

    ClosetsThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, ClosetsPostSearchCategoryAllRequestDto req);

    ClosetsThumbnailsResponseDto findImagesByCategoryLS(Pageable pageable, ClosetsPostSearchCategoryLSRequestDto req);

    //ClosetsThumbnailsResponseDto findImagesByCreatedDateDescDisplay();

}
