package com.betweenourclothes.service.stores;

import com.betweenourclothes.web.dto.request.stores.*;
import com.betweenourclothes.web.dto.response.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.StoresThumbnailsResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoresService {

    /*** 게시글
     * 1. post: 등록
     * 2. update: 수정
     * 3. delete: 삭제
     * 4. findPostById: 게시글 ID로 게시글 가져오기
     * 5. findImagesByAllCategory: 게시글 미리보기 가져오기
     * ***/

    Long post(StoresPostRequestDto postinfo, StoresPostClothesRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs);
    void update(Long id, StoresPostRequestDto postinfo, StoresPostClothesRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs);
    void delete(Long id);
    StoresPostResponseDto findPostById(Long id);
    StoresThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, StoresSearchCategoryAllRequestDto req);


    /*** 댓글
     * 1. comment: 등록
     * 2. findStoresCommentsByPostId: 게시글 ID로 댓글 가져오기
     * ***/

    void comment(Long id, StoresPostCommentRequestDto requestDto);
    StoresPostCommentsResponseDto findStoresCommentsByPostId(Long id);


    /*** 찜
     * 1. 찜 등록
     * 2. 찜 가져오기
     * ***/

    void likes(Long id);
    void undo_likes(Long id);
    StoresThumbnailsResponseDto findStoresLikesByMember(Pageable pageable);


    /*** 게시글 검색
     * 1. 검색
     * ***/
}
