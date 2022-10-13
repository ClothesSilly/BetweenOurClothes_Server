package com.betweenourclothes.web;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignInException;
import com.betweenourclothes.service.auth.AuthServiceImpl;
import com.betweenourclothes.web.dto.*;
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

    private final AuthServiceImpl authService;

    @PostMapping("/sign-up/image")
    public ResponseEntity<String> upload(@RequestParam(name = "image", required = false) MultipartFile img) throws IOException {

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

            return new ResponseEntity<>(file.getAbsolutePath(), HttpStatus.OK);
        } catch(NullPointerException e){
            throw new AuthSignInException(ErrorCode.REQUEST_FORMAT_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody @Valid AuthSignUpRequestDto requestDto) throws Exception {
        authService.signUp(requestDto);
        return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-up/email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid AuthEmailRequestDto requestDto) throws Exception{
        authService.sendMail(requestDto);
        return new ResponseEntity<>("이메일 전송 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-up/code")
    public ResponseEntity<String> checkAuthCode(@RequestBody @Valid AuthEmailRequestDto requestDto) throws Exception{
        authService.checkAuthCode(requestDto);
        return new ResponseEntity<>("인증 성공", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDto> login(@RequestBody AuthSignInRequestDto requestDto) throws Exception{
        AuthTokenResponseDto responseDto = authService.login(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/issue")
    public ResponseEntity<AuthTokenResponseDto> issueToken(@RequestBody AuthTokenRequestDto requestDto) throws Exception{
        AuthTokenResponseDto responseDto = authService.issueToken(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
