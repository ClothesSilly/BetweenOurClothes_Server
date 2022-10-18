package com.betweenourclothes.web;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignInException;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import org.springframework.web.bind.annotation.*;
import com.betweenourclothes.service.closets.ClosetsService;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/closets")
public class ClosetsApiController {
    private final ClosetsService closetsService;

    @PostMapping("/post")
    public ResponseEntity<String> post(@RequestBody ClosetsPostRequestDto requestDto){
        closetsService.post(requestDto);
        return new ResponseEntity<>("등록 완료", HttpStatus.OK);
    }

    @PostMapping("/post/images")
    public ResponseEntity<List> upload(@RequestParam(name = "image", required = false) List<MultipartFile> images) throws IOException {

        List<String> pathArr = new ArrayList<>();
        for(MultipartFile img: images){
            try{
                // 업로드 파일 이름 생성
                // 업로드 파일 식별을 위한 uuid 생성
                String uuid = UUID.randomUUID().toString();
                String path = new File("./src/main/resources/static/images/closets").getAbsolutePath();
                String uploadedFileName = "closets-" + uuid;

                // 확장자
                String extension = '.' + img.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");

                // 파일 객체 생성: 파일이 저장될 디렉토리를 만듦
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }

                // 파일 객체 생성: 업로드될 파일을 위한 것
                file = new File(path+"/"+uploadedFileName+extension);


                // 전송 후, 파일 경로 저장
                img.transferTo(file);
                pathArr.add(file.getAbsolutePath());
            } catch(NullPointerException e){
                throw new ClosetsPostException(ErrorCode.REQUEST_FORMAT_ERROR);
            }
        }
        return new ResponseEntity<>(pathArr, HttpStatus.OK);
    }
}
