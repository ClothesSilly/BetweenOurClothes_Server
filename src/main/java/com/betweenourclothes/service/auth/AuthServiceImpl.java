package com.betweenourclothes.service.auth;

import com.betweenourclothes.domain.members.Email;
import com.betweenourclothes.domain.members.EmailRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.*;
import com.betweenourclothes.jwt.JwtStatus;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.*;
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
import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
    private final MembersRepository membersRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailRepository emailRepository;
    private final JavaMailSender sender;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void signUp(AuthSignUpRequestDto requestDto){

        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_EMAIL);
        }

        Email user = emailRepository.findByEmail(requestDto.getEmail()).orElseThrow(()->new AuthSignUpException(ErrorCode.USER_NOT_FOUND));
        if(!user.getStatus().equals("Y")){
            throw new AuthSignUpException(ErrorCode.NOT_AUTHENTICATED);
        }
        emailRepository.delete(user);

        if(membersRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_NICKNAME);
        }

        Members member= requestDto.toEntity();
        membersRepository.save(member);
        member.encodePassword(passwordEncoder);
    }

    @Transactional
    @Override
    public void sendMail(AuthEmailRequestDto requestDto){
        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_EMAIL);
        }

        MimeMessage message;
        try {
            message = createMessage(requestDto);
        }catch(Exception e){
            throw new AuthSignUpException(ErrorCode.MAIL_MSG_CREATION_ERROR);
        }

        try{
            System.out.println(message);
            sender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            throw new AuthSignUpException(ErrorCode.MAIL_REQUEST_ERROR);
        }
        emailRepository.save(requestDto.toEntity());
    }

    @Transactional
    @Override
    public void checkAuthCode(AuthEmailRequestDto receiver) {
        Email user = emailRepository.findByEmail(receiver.getEmail()).orElseThrow(()->new AuthSignUpException(ErrorCode.USER_NOT_FOUND));
        System.out.println("existed code: " + user.getCode());
        System.out.println("requested code: " + receiver.getCode());
        if(user.getCode().equals(receiver.getCode())){
            receiver.setStatusAccepted();
            user.updateStatus(receiver.getStatus());
            return;
        }
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

    @Transactional
    @Override
    public AuthTokenResponseDto login(AuthSignInRequestDto requestDto) {
        // 로그인 하려는 member 찾기
        Members member = membersRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new AuthSignInException(ErrorCode.USER_NOT_FOUND));

        // member 정보를 담은 token을 만듦
        // authenticationManager에게 전달
        // authenticate 메서드: DB에 있는 사용자 정보를 가져와 비밀번호 체크
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // authentication을 jwtTokenProvider에게 전달해 jwt 토큰을 만듦
        // DB에 refresh token 저장
        AuthTokenResponseDto responseDto = jwtTokenProvider.createToken(authentication);
        member.updateRefreshToken(responseDto.getRefreshToken());
        return responseDto;
    }

    @Transactional
    @Override
    public AuthTokenResponseDto issueToken(AuthTokenRequestDto requestDto) {

        // Refresh Token 상태 확인
        if (jwtTokenProvider.validateToken(requestDto.getRefreshToken()) != JwtStatus.ACCESS) {
            System.out.println("여기까지 오면 재로그인 진행해야함");
            throw new AuthTokenException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        // Access Token에서 유저 정보 확인
        // DB에서 해당 유저의 Refresh Token을 꺼내서
        // request로 받은 정보와 일치하는지 확인
        Authentication authentication = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        Members member = membersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthTokenException(ErrorCode.USER_NOT_FOUND));
        if (!member.getRefreshToken().equals(requestDto.getRefreshToken())) {
            throw new AuthTokenException(ErrorCode.WRONG_USER);
        }

        // 재발급
        // DB에 재발급된 Refresh Token 업데이트
        AuthTokenResponseDto responseDto = jwtTokenProvider.createToken(authentication);
        member.updateRefreshToken(responseDto.getRefreshToken());

        return responseDto;
    }

}
