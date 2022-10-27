package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
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

    ClosetsImagesResponseDto findImagesByPostId(Long id);

    ClosetsThumbnailsResponseDto findImagesByCategoryL(Pageable pageable, String name);

    ClosetsThumbnailsResponseDto findImagesByCategoryLS(Pageable pageable, String nameL, String nameS);

    //ClosetsThumbnailsResponseDto findImagesByCreatedDateDescDisplay();

}
