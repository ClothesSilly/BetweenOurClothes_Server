package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.auth.AuthOnlyEmailRequestDto;
import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.auth.AuthSignUpRequestDto;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignInException;
import com.betweenourclothes.service.auth.AuthServiceImpl;
import com.betweenourclothes.web.dto.request.auth.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.response.auth.AuthSignInResponseDto;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthServiceImpl authService;

    @ApiOperation(value="최종 회원가입", notes="Models > AuthSignUpRequestDto 참고")
    @PostMapping(path="/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestPart(name="data") AuthSignUpRequestDto requestDto,
                                         @RequestPart(name="image") MultipartFile img) throws Exception {
        String path = convertFile2Path(img);
        authService.signUp(requestDto, path);
        return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
    }

    public String convertFile2Path(MultipartFile img){
        try{
            // 업로드 파일 이름 생성
            // 업로드 파일 식별을 위한 uuid 생성
            String uuid = UUID.randomUUID().toString();
            String homePath = System.getProperty("user.home");
            String path = new File(homePath +"/betweenourclothes/images/profile").getAbsolutePath();
            String uploadedFileName = "profile-" + uuid;

            // 확장자
            String extension = '.' + img.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");

            // 파일 객체 생성: 파일이 저장될 디렉토리를 만듦
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }

            // 파일 객체 생성: 업로드될 파일을 위한 것
            file = new File(path+File.separator+uploadedFileName+extension);

            // 전송 후, 파일 경로 반환
            img.transferTo(file);
            return file.getAbsolutePath();
        } catch(NullPointerException e){
            throw new AuthSignInException(ErrorCode.REQUEST_FORMAT_ERROR);
        } catch (IOException e) {
            throw new AuthSignInException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }

    @ApiOperation(value="이메일 인증코드 발송", notes="Models > AuthOnlyEmailRequestDto 참고")
    @PostMapping("/sign-up/email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid AuthOnlyEmailRequestDto email) throws Exception{
        authService.sendMail(email.getEmail());
        return new ResponseEntity<>("이메일 전송 성공", HttpStatus.OK);
    }


    @ApiOperation(value="이메일 인증코드 일치여부 확인", notes="Models > AuthEmailRequestDto 참고")
    @PostMapping("/sign-up/code")
    public ResponseEntity<String> checkAuthCode(@RequestBody @Valid AuthEmailRequestDto requestDto) throws Exception{
        authService.checkAuthCode(requestDto);
        return new ResponseEntity<>("인증 성공", HttpStatus.OK);
    }

    @ApiOperation(value="로그인", notes="Models > AuthSignInRequestDto 참고")
    @PostMapping("/login")
    public ResponseEntity<AuthSignInResponseDto> login(@RequestBody AuthSignInRequestDto requestDto) throws Exception{
        AuthSignInResponseDto responseDto = authService.login(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @ApiOperation(value="토큰 재발급", notes="header의 ACCESS_TOKEN: accessToken, REFRESH_TOKEN: refreshToken")
    @PostMapping("/issue")
    public ResponseEntity<AuthTokenResponseDto> issueToken(HttpServletRequest request) throws Exception{
        AuthTokenResponseDto responseDto = authService.issueToken(request);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
