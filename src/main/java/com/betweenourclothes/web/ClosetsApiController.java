package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsThumbnailsResponseDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import com.betweenourclothes.service.closets.ClosetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/closets")
public class ClosetsApiController {
    private final ClosetsService closetsService;

    /*** 게시글 등록 ***/
    @PostMapping(path="/post")
    @ApiOperation(value="게시글 등록", notes="게시글 id return, 빈 문자열 넣기")
    public ResponseEntity<String> post(@RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = closetsService.post(requestDto, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }


    /*** 게시글 수정 ***/
    @PatchMapping("/post/{id}")
    @ApiOperation(value="게시글 수정", notes="없는 경우 빈 문자열 넣기")
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                         @RequestPart(name="image") List<MultipartFile> imgs){
        closetsService.update(id, requestDto, imgs);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }


    /*** 게시글 삭제 ***/
    @DeleteMapping("/post/{id}")
    @ApiOperation(value="게시글 삭제")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        closetsService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }


    /*** 게시글 조회 ***/
    // 1. post id로 게시글 찾아오기 (full image)
    @GetMapping("/post/{id}")
    @ApiOperation(value="게시글 찾아오기", notes="게시글 이미지 반환, 게시글 id로 게시글 찾아오기")
    public ResponseEntity<ClosetsImagesResponseDto> findPostById(@PathVariable("id") Long id){
        ClosetsImagesResponseDto responseDto = closetsService.findPostById(id);
        return new ResponseEntity<ClosetsImagesResponseDto>(responseDto, HttpStatus.OK);
    }


    // 2. 카테고리 필터링 (300x300)
    @GetMapping("/post/category")
    @ApiOperation(value="게시글 필터링", notes="썸네일 반환, 없는 경우 null 넣기! 전부 null일 경우 전체 아이템 가져옴")
    public ResponseEntity<ClosetsThumbnailsResponseDto> findImagesByAllCategory(@PageableDefault(size=15) Pageable pageable,
                                                                                @RequestBody ClosetsSearchCategoryAllRequestDto req){
        ClosetsThumbnailsResponseDto responseDto = closetsService.findImagesByAllCategory(pageable, req);
        return new ResponseEntity<ClosetsThumbnailsResponseDto>(responseDto, HttpStatus.OK);
    }

    /*
    @GetMapping("/post/thumbnails/display")
    public ResponseEntity<byte[]> findThumbnailsDisplay(){
        ClosetsThumbnailsResponseDto responseDto = closetsService.findImagesByCreatedDateDescDisplay();

        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "image/jpeg");

        return new ResponseEntity<byte[]>(responseDto.getImages().get(0), header, HttpStatus.OK);
    }
*/
}
