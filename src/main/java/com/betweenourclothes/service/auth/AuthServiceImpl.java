package com.betweenourclothes.service.auth;

import com.betweenourclothes.domain.auth.Email;
import com.betweenourclothes.domain.auth.EmailRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.members.Role;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.*;
import com.betweenourclothes.jwt.JwtStatus;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.request.auth.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.auth.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.response.auth.AuthSignInResponseDto;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
    private final MembersRepository membersRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailRepository emailRepository;
    private final JavaMailSender sender;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    /*** 회원가입 ***/

    @Transactional
    @Override
    public void signUp(AuthSignUpRequestDto requestDto, String imgPath) throws UnsupportedEncodingException {


        // 이메일 중복 체크
        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 이메일 인증상태 체크 후
        // 임시테이블에서 삭제
        Email user = emailRepository.findByEmail(requestDto.getEmail()).orElseThrow(()->new AuthSignUpException(ErrorCode.USER_NOT_FOUND));
        if(!user.getStatus().equals("Y")){
            throw new AuthSignUpException(ErrorCode.NOT_AUTHENTICATED);
        }
        emailRepository.delete(user);

        // 닉네임 중복체크
        if(membersRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 비밀번호 인코딩 후
        // Member 테이블에 저장
        Members member= requestDto.toEntity(imgPath, Role.ROLE_USER);
        member.encodePassword(passwordEncoder);
        membersRepository.save(member);
    }


    /*** 이메일 인증 & 체크 ***/

    @Transactional
    @Override
    public void sendMail(String email)  {
        // 이메일 중복 체크

        if(membersRepository.findByEmail(email).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_EMAIL);
        }

        AuthEmailRequestDto requestDto = null;
        try {
            requestDto = AuthEmailRequestDto.builder().email(email).build();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        requestDto.setAuthCode(requestDto.createCode());

        // 이메일 인증코드 생성: Dto 객체가 생성될 때 생성함
        // 이메일 메시지 생성
        MimeMessage message;
        try {
            message = createMessage(requestDto);
        }catch(Exception e){
            throw new AuthSignUpException(ErrorCode.MAIL_MSG_CREATION_ERROR);
        }

        // 이메일 전송
        try{
            //System.out.println(message);
            sender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            throw new AuthSignUpException(ErrorCode.MAIL_REQUEST_ERROR);
        }

        // 임시테이블에 저장
        emailRepository.save(requestDto.toEntity(false));
    }

    @Transactional
    @Override
    public void checkAuthCode(AuthEmailRequestDto receiver) {
        // 임시 테이블 조회

        Email user = emailRepository.findByEmail(receiver.getEmail()).orElseThrow(()->new AuthSignUpException(ErrorCode.USER_NOT_FOUND));
        //System.out.println("existed code: " + user.getCode());
        //System.out.println("requested code: " + receiver.getCode());

        // 코드 일치 여부 확인 후
        // 임시 테이블의 상태 업데이트
        if(user.getCode().equals(receiver.getCode())){
            user.updateStatus("Y");
            return;
        }
        // 일치하지 않으면 예외 던지기
        throw new AuthSignUpException(ErrorCode.NOT_AUTHENTICATED);
    }

    private MimeMessage createMessage(AuthEmailRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = sender.createMimeMessage();
        msg.addRecipients(Message.RecipientType.TO, requestDto.getEmail());
        msg.setSubject("너와 내 옷 사이 회원가입 인증번호가 도착했습니다.");

        String body = "<div style='margin:100px;'>\n" +
                "<h1> 안녕하세요. 너와 내 옷 사이입니다.</h1>\n" +
                "<br>\n" +
                "<p>아래 인증코드를 회원가입 창으로 돌아가 입력해주세요.<p>\n" +
                "<p>감사합니다!<p>\n" +
                "<br>\n" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>\n" +
                "<br>\n" +
                "<div style='font-size:130%'>인증코드 : \n" +
                "<strong>"+ requestDto.getCode() +"\n" +
                "</strong><div><br/></div>";

        msg.setText(body, "utf-8", "html");
        msg.setFrom(new InternetAddress("gunsong2@gmail.com", "너와 내 옷 사이"));
        return msg;
    }


    /*** 로그인 ***/

    @Transactional
    @Override
    public AuthSignInResponseDto login(AuthSignInRequestDto requestDto) {


        // 로그인 하려는 member 찾기
        Members member = membersRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new AuthSignInException(ErrorCode.USER_NOT_FOUND));

        // member 정보를 담은 token을 만듦
        // authenticationManager에게 전달
        // authenticate 메서드: DB에 있는 사용자 정보를 가져와 비밀번호 체크
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // authentication을 jwtTokenProvider에게 전달해 jwt 토큰을 만듦
        // DB에 refresh token 저장
        // jwt 토큰 리턴
        AuthTokenResponseDto token = jwtTokenProvider.createToken(authentication);
        AuthSignInResponseDto responseDto = AuthSignInResponseDto.builder().image(member.toByte(300, 300))
                .nickname(member.getNickname()).grantType(token.getGrantType()).accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken()).build();

        member.updateRefreshToken(token.getRefreshToken());
        return responseDto;
    }


    /*** 토큰 재발급 ***/

    @Transactional
    @Override
    public AuthTokenResponseDto issueToken(HttpServletRequest request) {

        String accessToken = request.getHeader("ACCESS_TOKEN");
        String refreshToken = request.getHeader("REFRESH_TOKEN");

        // Refresh Token 상태 확인
        if (jwtTokenProvider.validateToken(refreshToken) != JwtStatus.ACCESS) {
            System.out.println("여기까지 오면 재로그인 진행해야함");
            throw new AuthTokenException(ErrorCode.REFRESH_TOKEN_ERROR);
        }


        // Access Token에서 유저 정보 확인
        // DB에서 해당 유저의 Refresh Token을 꺼내서
        // request로 받은 정보와 일치하는지 확인
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        Members member = membersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthTokenException(ErrorCode.USER_NOT_FOUND));
        if (!member.getRefreshToken().equals(refreshToken)) {
            throw new AuthTokenException(ErrorCode.WRONG_USER);
        }

        // 재발급
        // DB에 재발급된 Refresh Token 업데이트
        AuthTokenResponseDto responseDto = jwtTokenProvider.createToken(authentication);
        member.updateRefreshToken(responseDto.getRefreshToken());

        // jwt 토큰 리턴
        return responseDto;
    }

}
