package com.betweenourclothes.service.stores;

import com.betweenourclothes.web.dto.request.stores.*;
import com.betweenourclothes.web.dto.response.stores.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
import org.springframework.data.domain.Page;
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
    Page<StoresThumbnailsResponseDto> findPostsByAllCategory(Pageable pageable, StoresSearchCategoryAllRequestDto req);


    /*** 댓글
     * 1. comment: 등록
     * 2. findStoresCommentsByPostId: 게시글 ID로 댓글 가져오기
     * ***/

    void comment(Long id, StoresPostCommentRequestDto requestDto);
    Page<StoresPostCommentsResponseDto> findStoresCommentsByPostId(Pageable pageable, Long id);


    /*** 찜
     * 1. 찜 등록
     * 2. 찜 삭제
     * 3. 찜 가져오기
     * ***/

    void likes(Long id);
    void undo_likes(Long id);
    Page<StoresThumbnailsResponseDto> findStoresLikesByMember(Pageable pageable);


    /*** 게시글 검색
     * 1. 검색
     * ***/
    Page<StoresThumbnailsResponseDto> findByKeyword(Pageable pageable, StoresPostSearchRequestDto requestDto);


    /*** 판매 상태
     * 1. 판매 상태 업데이트
     * ***/
    void updateSalesStatus(Long id);
}
