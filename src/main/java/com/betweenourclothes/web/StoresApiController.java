package com.betweenourclothes.web;

import com.betweenourclothes.service.stores.StoresService;
import com.betweenourclothes.web.dto.request.stores.*;
import com.betweenourclothes.web.dto.response.stores.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
     * 5. findPostsByAllCategory: 게시글 미리보기 가져오기
     * ***/

    // 게시글 등록
    @PostMapping("/post")
    @ApiOperation(value="게시글 등록")
    public ResponseEntity<String> post(@RequestPart(name="post_data") StoresPostRequestDto postinfo,
                                       @RequestPart(name="clothes_data") StoresPostClothesRequestDto clothesinfo,
                                       @RequestPart(name="sales_data") StoresPostSalesRequestDto salesinfo,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = storesService.post(postinfo, clothesinfo, salesinfo, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }

    // 게시글 수정
    @PatchMapping("/post/{id}")
    @ApiOperation(value="게시글 수정")
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
    @ApiOperation(value="게시글 삭제")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        storesService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    // 게시글 ID로 게시글 가져오기
    @GetMapping("/post/{id}")
    @ApiOperation(value="게시글 ID로 가져오기")
    public ResponseEntity<StoresPostResponseDto> findPostById(@PathVariable("id") Long id){
        StoresPostResponseDto responseDto = storesService.findPostById(id);
        return new ResponseEntity<StoresPostResponseDto>(responseDto, HttpStatus.OK);
    }

    // 게시글 미리보기 가져오기
    @GetMapping("/post/category")
    @ApiOperation(value="게시글 미리보기 가져오기")
    public ResponseEntity<Page<StoresThumbnailsResponseDto>> findPostsByAllCategory(@PageableDefault(size=15) Pageable pageable,
                                                                               @RequestBody StoresSearchCategoryAllRequestDto req){
        Page<StoresThumbnailsResponseDto> responseDto = storesService.findPostsByAllCategory(pageable, req);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /*** 댓글
     * 1. comment: 등록
     * 2. findStoresCommentsByPostId: 게시글 ID로 댓글 가져오기
     * ***/

    // 댓글 등록
    @PostMapping("/post/{id}/comment")
    @ApiOperation(value="댓글 등록")
    public ResponseEntity<String> comment(@PathVariable("id") Long id, @RequestBody StoresPostCommentRequestDto comments){
        storesService.comment(id, comments);
        return new ResponseEntity<>("댓글 등록 완료", HttpStatus.OK);
    }

    // 게시글 ID로 댓글 가져오기
    @GetMapping("/post/{id}/comment")
    @ApiOperation(value="댓글 가져오기")
    public ResponseEntity<Page<StoresPostCommentsResponseDto>> findStoresCommentsByPostId(@PageableDefault(size=20) Pageable pageable,
                                                                                          @PathVariable("id") Long id){
        Page<StoresPostCommentsResponseDto> responseDto = storesService.findStoresCommentsByPostId(pageable, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    /*** 찜
     * 1. 찜 등록
     * 2. 찜 삭제
     * 3. 찜 가져오기
     * ***/
    @PostMapping("/post/{id}/like")
    @ApiOperation(value="찜 등록/삭제")
    public ResponseEntity<String> updateLikes(@PathVariable("id") Long id){
        storesService.likes(id);
        return new ResponseEntity<String>("찜 등록 완료", HttpStatus.OK);
    }

    @DeleteMapping("/post/{id}/like")
    @ApiOperation(value="찜 삭제")
    public ResponseEntity<String> undoLikes(@PathVariable("id") Long id){
        storesService.undo_likes(id);
        return new ResponseEntity<String>("찜 취소 완료", HttpStatus.OK);
    }

    @GetMapping("/post/like")
    @ApiOperation(value="찜 가져오기")
    public ResponseEntity<Page<StoresThumbnailsResponseDto>> findStoresLikesByMember(@PageableDefault(size=15) Pageable pageable){
        Page<StoresThumbnailsResponseDto> responseDto = storesService.findStoresLikesByMember(pageable);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /*** 판매 상태
     * 1. 판매 상태 업데이트
     * ***/
    @PutMapping("/post/{id}/status")
    @ApiOperation(value="판매중/판매완료 업데이트")
    public ResponseEntity<String> updateSalesStatus(@PathVariable("id") Long id){
        storesService.updateSalesStatus(id);
        return new ResponseEntity<>("판매 상태 업데이트 완료", HttpStatus.OK);
    }

    /*** 게시글 검색
     * 1. 검색
     * ***/
    @GetMapping("/post/search")
    @ApiOperation(value="게시글 키워드로 검색")
    public ResponseEntity<Page<StoresThumbnailsResponseDto>> findByKeyword(@PageableDefault(size=15) Pageable pageable, @RequestBody StoresPostSearchRequestDto requestDto){
        Page<StoresThumbnailsResponseDto> responseDto = storesService.findByKeyword(pageable, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
