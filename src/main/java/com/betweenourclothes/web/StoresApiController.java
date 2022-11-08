package com.betweenourclothes.web;

import com.betweenourclothes.service.stores.StoresService;
import com.betweenourclothes.web.dto.request.stores.*;
import com.betweenourclothes.web.dto.response.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.StoresThumbnailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoresApiController {

    private final StoresService storesService;

    /*** 게시글
     * 1. post: 등록
     * 2. update: 수정
     * 3. delete: 삭제
     * 4. findPostById: 게시글 ID로 게시글 가져오기
     * 5. findImagesByAllCategory: 게시글 미리보기 가져오기
     * ***/

    // 게시글 등록
    @PostMapping("/post")
    public ResponseEntity<String> post(@RequestPart(name="post_data") StoresPostRequestDto postinfo,
                                       @RequestPart(name="clothes_data") StoresPostClothesRequestDto clothesinfo,
                                       @RequestPart(name="sales_data") StoresPostSalesRequestDto salesinfo,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = storesService.post(postinfo, clothesinfo, salesinfo, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }

    // 게시글 수정
    @PatchMapping("/post/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestPart(name="post_data") StoresPostRequestDto postinfo,
                                         @RequestPart(name="clothes_data") StoresPostClothesRequestDto clothesinfo,
                                         @RequestPart(name="sales_data") StoresPostSalesRequestDto salesinfo,
                                         @RequestPart(name="image") List<MultipartFile> imgs){
        storesService.update(id, postinfo, clothesinfo, salesinfo, imgs);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        storesService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    // 게시글 ID로 게시글 가져오기
    @GetMapping("/post/{id}")
    public ResponseEntity<StoresPostResponseDto> findPostById(@PathVariable("id") Long id){
        StoresPostResponseDto responseDto = storesService.findPostById(id);
        return new ResponseEntity<StoresPostResponseDto>(responseDto, HttpStatus.OK);
    }

    // 게시글 미리보기 가져오기
    @GetMapping("/post/category")
    public ResponseEntity<StoresThumbnailsResponseDto> findImagesByAllCategory(@PageableDefault(size=15) Pageable pageable,
                                                                               @RequestBody StoresSearchCategoryAllRequestDto req){
        StoresThumbnailsResponseDto responseDto = storesService.findImagesByAllCategory(pageable, req);
        return new ResponseEntity<StoresThumbnailsResponseDto>(responseDto, HttpStatus.OK);
    }

    /*** 댓글
     * 1. comment: 등록
     * 2. findStoresCommentsByPostId: 게시글 ID로 댓글 가져오기
     * ***/

    // 댓글 등록
    @PostMapping("/post/{id}/comment")
    public ResponseEntity<String> comment(@PathVariable("id") Long id, @RequestBody StoresPostCommentRequestDto comments){
        storesService.comment(id, comments);
        return new ResponseEntity<>("댓글 등록 완료", HttpStatus.OK);
    }

    // 게시글 ID로 댓글 가져오기
    @GetMapping("/post/{id}/comment")
    public ResponseEntity<StoresPostCommentsResponseDto> findStoresCommentsByPostId(@PathVariable("id") Long id){
        StoresPostCommentsResponseDto responseDto = storesService.findStoresCommentsByPostId(id);
        return new ResponseEntity<StoresPostCommentsResponseDto>(responseDto, HttpStatus.OK);
    }


    /*** 찜
     * 1. 찜 등록
     * 2. 찜 삭제
     * 3. 찜 가져오기
     * ***/
    @PostMapping("/post/{id}/like")
    public ResponseEntity<String> likes(@PathVariable("id") Long id){
        storesService.likes(id);
        return new ResponseEntity<String>("찜 등록 완료", HttpStatus.OK);
    }

    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<String> undo_likes(@PathVariable("id") Long id){
        storesService.undo_likes(id);
        return new ResponseEntity<String>("찜 취소 완료", HttpStatus.OK);
    }

    @GetMapping("/post/like")
    public ResponseEntity<StoresThumbnailsResponseDto> findStoresLikesByMember(@PageableDefault(size=1) Pageable pageable){
        StoresThumbnailsResponseDto responseDto = storesService.findStoresLikesByMember(pageable);
        return new ResponseEntity<StoresThumbnailsResponseDto>(responseDto, HttpStatus.OK);
    }

    /*** 판매 상태
     * 1. 판매 상태 업데이트
     * ***/

    /*** 게시글 검색
     * 1. 검색
     * ***/


}
