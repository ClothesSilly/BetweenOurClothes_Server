package com.betweenourclothes.service.auth;

import com.betweenourclothes.domain.members.Email;
import com.betweenourclothes.domain.members.EmailRepository;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.*;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.AuthEmailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final EmailRepository emailRepository;
    private final JavaMailSender sender;

    @Transactional
    @Override
    public Long signUp(AuthSignUpRequestDto requestDto){

        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_EMAIL);
        }

        Email user = emailRepository.findByEmail(requestDto.getEmail()).orElseThrow(()->new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        if(!user.getStatus().equals("Y")){
            throw new NotAuthenticatedException(ErrorCode.NOT_AUTHENTICATED);
        }
        emailRepository.delete(user);

        if(membersRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_NICKNAME);
        }

        return membersRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    @Override
    public void sendMail(AuthEmailRequestDto requestDto){
        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new DuplicatedDataException(ErrorCode.DUPLICATE_EMAIL);
        }

        MimeMessage message;
        try {
            message = createMessage(requestDto);
        }catch(Exception e){
            throw new MailMsgCreationException(ErrorCode.MAIL_MSG_CREATION_ERROR);
        }

        try{
            System.out.println(message);
            sender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            throw new MailRequestException(ErrorCode.MAIL_REQUEST_ERROR);
        }
        emailRepository.save(requestDto.toEntity());
    }

    @Transactional
    @Override
    public void checkAuthCode(AuthEmailRequestDto receiver) {
        Email user = emailRepository.findByEmail(receiver.getEmail()).orElseThrow(()->new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        System.out.println("existed code: " + user.getCode());
        System.out.println("requested code: " + receiver.getCode());
        if(user.getCode().equals(receiver.getCode())){
            receiver.setStatusAccepted();
            user.update(receiver.getStatus());
            return;
        }
        throw new NotAuthenticatedException(ErrorCode.NOT_AUTHENTICATED);
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
}
