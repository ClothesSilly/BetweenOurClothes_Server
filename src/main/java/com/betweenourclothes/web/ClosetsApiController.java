package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsThumbnailsResponseDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
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

    /*** 게시글
     * 1. post: 등록
     * 2. update: 수정
     * 3. delete 삭제
     * 4. findPostById: 게시글 ID로 게시글 가져오기
     * 5. findPostsByAllCategory: 게시글 미리보기 가져오기
     * ***/
    @PostMapping(path="/post")
    @ApiOperation(value="게시글 등록", notes="Models > ClosetsPostRequestDto, 게시글 ID 리턴")
    public ResponseEntity<String> post(@RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = closetsService.post(requestDto, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }

    @PutMapping("/post/{id}")
    @ApiOperation(value="게시글 수정", notes="Models > ClosetsPostRequestDto, 게시글 ID Path Variable로 넘김")
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                         @RequestPart(name="image") List<MultipartFile> imgs){
        closetsService.update(id, requestDto, imgs);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }

    @DeleteMapping("/post/{id}")
    @ApiOperation(value="게시글 삭제", notes="게시글 ID Path Variable로 넘김")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        closetsService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    @ApiOperation(value="게시글 찾아오기", notes="게시글 ID Path Variable로 넘김")
    public ResponseEntity<ClosetsImagesResponseDto> findPostById(@PathVariable("id") Long id){
        ClosetsImagesResponseDto responseDto = closetsService.findPostById(id);
        return new ResponseEntity<ClosetsImagesResponseDto>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/post/category")
    @ApiOperation(value="게시글 필터링", notes="Models > ClosetsSearchCategoryAllRequestDto, 300x300 썸네일 반환, 없는 경우 null 넣기, 전부 null일 경우 전체 아이템 가져옴")
    public ResponseEntity<Page<ClosetsThumbnailsResponseDto>> findPostsByAllCategory(@PageableDefault(size=15) Pageable pageable,
                                                                                    @RequestBody ClosetsSearchCategoryAllRequestDto req){
        Page<ClosetsThumbnailsResponseDto> responseDto = closetsService.findPostsByAllCategory(pageable, req);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
