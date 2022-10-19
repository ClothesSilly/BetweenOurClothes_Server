package com.betweenourclothes.web;

import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignUpRequestDto;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignInException;
import com.betweenourclothes.service.auth.AuthServiceImpl;
import com.betweenourclothes.web.dto.request.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthServiceImpl authService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestPart(name="data") AuthSignUpRequestDto requestDto,
                                         @RequestPart(name="image") MultipartFile img) throws Exception {
        authService.signUp(requestDto, img);
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
    public ResponseEntity<AuthTokenResponseDto> issueToken(HttpServletRequest request) throws Exception{
        AuthTokenResponseDto responseDto = authService.issueToken(request);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
