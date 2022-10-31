package com.betweenourclothes.web;

import com.betweenourclothes.service.stores.StoresService;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import com.betweenourclothes.web.dto.request.StoresSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import com.betweenourclothes.web.dto.response.StoresImagesResponseDto;
import com.betweenourclothes.web.dto.response.StoresThumbnailsResponseDto;
import io.swagger.annotations.ApiOperation;
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

    /*** 게시글 등록 ***/
    @PostMapping("/post")
    public ResponseEntity<String> post(@RequestPart(name="data") StoresPostRequestDto requestDto,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = storesService.post(requestDto, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }

    /*** 게시글 수정 ***/
    @PatchMapping("/post/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestPart(name="data") StoresPostRequestDto requestDto,
                                         @RequestPart(name="image") List<MultipartFile> imgs){
        storesService.update(id, requestDto, imgs);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }

    /*** 게시글 삭제 ***/
    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        storesService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    /*** 게시글 가져오기 by post id ***/
    @GetMapping("/post/{id}")
    public ResponseEntity<StoresImagesResponseDto> findPostById(@PathVariable("id") Long id){
        StoresImagesResponseDto responseDto = storesService.findPostById(id);
        return new ResponseEntity<StoresImagesResponseDto>(responseDto, HttpStatus.OK);
    }

    /*** 옷 필터 ***/
    @GetMapping("/post/category")
    public ResponseEntity<StoresThumbnailsResponseDto> findImagesByAllCategory(@PageableDefault(size=15) Pageable pageable,
                                                                               @RequestBody StoresSearchCategoryAllRequestDto req){
        StoresThumbnailsResponseDto responseDto = storesService.findImagesByAllCategory(pageable, req);
        return new ResponseEntity<StoresThumbnailsResponseDto>(responseDto, HttpStatus.OK);
    }

}
