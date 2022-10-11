package com.betweenourclothes.web;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.RequestFormatException;
import com.betweenourclothes.service.auth.AuthServiceImpl;
import com.betweenourclothes.web.dto.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthServiceImpl membersService;

    @PostMapping("/sign-up/image")
    public String upload(@RequestParam(name = "image", required = false) MultipartFile img) throws IOException {

        try{
            String uuid = UUID.randomUUID().toString();
            String path = new File("./src/main/resources/static/images/profile").getAbsolutePath();
            String uploadedFileName = "profile-" + uuid;

            String extension = '.' + img.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }

            file = new File(path+"/"+uploadedFileName+extension);
            img.transferTo(file);

            return file.getAbsolutePath();
        } catch(NullPointerException e){
            throw new RequestFormatException(ErrorCode.REQUEST_FORMAT_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public Long signUp(@RequestBody @Valid AuthSignUpRequestDto requestDto) throws Exception {
        return membersService.signUp(requestDto);
    }

    @PostMapping("/sign-up/email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid AuthEmailRequestDto requestDto) throws Exception{
        membersService.sendMail(requestDto);
        return new ResponseEntity<>("이메일 전송 성공", HttpStatus.OK);
    }

    /*@PostMapping("/sign-up/code")
    public ResponseEntity checkAuthCode(@RequestBody @Valid AuthEmailRequestDto requestDto) throws Exception{
        return null;
    }*/
}
