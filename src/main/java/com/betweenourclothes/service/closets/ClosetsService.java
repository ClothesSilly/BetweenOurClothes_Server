package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsThumbnailsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClosetsService {

    /*** 게시글
     * 1. post: 등록
     * 2. update: 수정
     * 3. delete: 삭제
     * 4. findPostById: 게시글 ID로 게시글 가져오기
     * 5. findPostsByAllCategory: 게시글 미리보기 가져오기
     * ***/

    Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);
    // 추천항목 등록
    void update(Long id, ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);
    void delete(Long id);
    ClosetsImagesResponseDto findPostById(Long id);
    Page<ClosetsThumbnailsResponseDto> findPostsByAllCategory(Pageable pageable, ClosetsSearchCategoryAllRequestDto req);


}
