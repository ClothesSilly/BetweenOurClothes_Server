package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.response.ThumbnailsResponseDto;
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
    ThumbnailsResponseDto findImagesByCreatedDateDesc();





    //ThumbnailsResponseDto findImagesByCreatedDateDescDisplay();

}
