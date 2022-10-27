package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
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

    @PostMapping("/post")
    public ResponseEntity<String> post(@RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                       @RequestPart(name="image") List<MultipartFile> imgs){
        Long id = closetsService.post(requestDto, imgs);
        return new ResponseEntity<>(id.toString(), HttpStatus.OK);
    }

    @PatchMapping("/post/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestPart(name="data") ClosetsPostRequestDto requestDto,
                                         @RequestPart(name="image") List<MultipartFile> imgs){
        closetsService.update(id, requestDto, imgs);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        closetsService.delete(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }


    // GET
    @GetMapping("/post/thumbnails")
    public ResponseEntity<ClosetsThumbnailsResponseDto> findThumbnails(@PageableDefault(size=15) Pageable pageable){
        ClosetsThumbnailsResponseDto responseDto = closetsService.findImagesByCreatedDateDesc(pageable);

        //HttpHeaders header = new HttpHeaders();
        //header.set("Content-Type", "image/jpeg");

        return new ResponseEntity<ClosetsThumbnailsResponseDto>(responseDto, HttpStatus.OK);
    }

    // id로 게시글 찾아오기
    @GetMapping("/post/{id}")
    public ResponseEntity<ClosetsImagesResponseDto> findPostById(@PathVariable("id") Long id){
        ClosetsImagesResponseDto responseDto = closetsService.findImagesByPostId(id);
        return new ResponseEntity<ClosetsImagesResponseDto>(responseDto, HttpStatus.OK);
    }


    // 큰 카테고리
    public ResponseEntity<ClosetsThumbnailsResponseDto> findByCategoryL(@PageableDefault(size=15) Pageable pageable){
        return null;
    }

    // 큰 카테고리, 작은 카테고리
    public ResponseEntity<ClosetsThumbnailsResponseDto> findByCategory(@PageableDefault(size=15) Pageable pageable){
        return null;
    }

    // 큰 카테고리, 작은 카테고리, 기장, 핏, 재질, 색상
    public ResponseEntity<ClosetsThumbnailsResponseDto> findByCategoryAndClothesInfo(@PageableDefault(size=15) Pageable pageable){
        return null;
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
