package com.betweenourclothes.service.auth;

import com.betweenourclothes.domain.members.Email;
import com.betweenourclothes.domain.members.EmailRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.*;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.AuthTokenInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    public AuthTokenInfoResponseDto login(AuthSignInRequestDto requestDto) {
        Members member = membersRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new AuthSignInException(ErrorCode.USER_NOT_FOUND));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        AuthTokenInfoResponseDto responseDto = jwtTokenProvider.createToken(authentication);
        member.updateRefreshToken(responseDto.getRefreshToken());
        return responseDto;
    }

}
