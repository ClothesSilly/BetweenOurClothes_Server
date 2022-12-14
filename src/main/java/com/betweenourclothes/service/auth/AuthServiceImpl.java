package com.betweenourclothes.service.auth;

import com.betweenourclothes.domain.auth.Authentication;
import com.betweenourclothes.domain.auth.AuthenticationRedisRepository;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
    private final MembersRepository membersRepository;
    private final AuthenticationManager authenticationManager;
    //private final AuthenticationRedisRepository authenticationRedisRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender sender;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    /*** ???????????? ***/

    @Transactional
    @Override
    public void signUp(AuthSignUpRequestDto requestDto, String imgPath) throws UnsupportedEncodingException {
        requestDto.getPassword();

        // ????????? ?????? ??????
        if(membersRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_EMAIL);
        }

        // ????????? ???????????? ?????? ???
        // ????????????????????? ??????
        //Authentication user = authenticationRedisRepository.findByEmail(requestDto.getEmail()).orElseThrow(()->new AuthSignUpException(ErrorCode.USER_NOT_FOUND));
        Authentication user = null;
        try{
            user = findAuthentication(requestDto.getEmail());
        } catch (Exception e){
            throw new AuthSignUpException(ErrorCode.USER_NOT_FOUND);
        }

        if(!user.getStatus().equals("Y")){
            throw new AuthSignUpException(ErrorCode.NOT_AUTHENTICATED);
        }
        redisTemplate.delete(user.getEmail());

        // ????????? ????????????
        if(membersRepository.findByNickname(requestDto.getNickname()).isPresent()){
            throw new AuthSignUpException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // ???????????? ????????? ???
        // Member ???????????? ??????
        Members member= requestDto.toEntity(imgPath, Role.ROLE_USER);
        member.encodePassword(passwordEncoder);
        membersRepository.save(member);
    }


    /*** ????????? ?????? & ?????? ***/

    @Transactional
    @Override
    public void sendMail(String email)  {
        // ????????? ?????? ??????

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

        // ????????? ???????????? ??????: Dto ????????? ????????? ??? ?????????
        // ????????? ????????? ??????
        MimeMessage message;
        try {
            message = createMessage(requestDto);
        }catch(Exception e){
            throw new AuthSignUpException(ErrorCode.MAIL_MSG_CREATION_ERROR);
        }

        // ????????? ??????
        try{
            //System.out.println(message);
            sender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            throw new AuthSignUpException(ErrorCode.MAIL_REQUEST_ERROR);
        }

        // ?????????????????? ??????
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, requestDto.toEntity(false));
        redisTemplate.expire(email, 15, TimeUnit.MINUTES);
    }

    @Transactional
    @Override
    public void checkAuthCode(AuthEmailRequestDto receiver) {
        // ?????? ????????? ??????
        Authentication user = null;
        try{
            user = findAuthentication(receiver.getEmail());
        } catch (Exception e){
            throw new AuthSignUpException(ErrorCode.USER_NOT_FOUND);
        }
        //authenticationRedisRepository.findByEmail(receiver.getEmail()).orElseThrow(()->new AuthSignUpException(ErrorCode.USER_NOT_FOUND));

        // ?????? ?????? ?????? ?????? ???
        // ?????? ???????????? ?????? ????????????
        if(user.getCode().equals(receiver.getCode())){
            user.updateStatus("Y");
            final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(user.getEmail(), user);
            return;
        }
        // ???????????? ????????? ?????? ?????????
        throw new AuthSignUpException(ErrorCode.NOT_AUTHENTICATED);
    }

    private MimeMessage createMessage(AuthEmailRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = sender.createMimeMessage();
        msg.addRecipients(Message.RecipientType.TO, requestDto.getEmail());
        msg.setSubject("?????? ??? ??? ?????? ???????????? ??????????????? ??????????????????.");

        String body = "<div style='margin:100px;'>\n" +
                "<h1> ???????????????. ?????? ??? ??? ???????????????.</h1>\n" +
                "<br>\n" +
                "<p>?????? ??????????????? ???????????? ????????? ????????? ??????????????????.<p>\n" +
                "<p>???????????????!<p>\n" +
                "<br>\n" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>\n" +
                "<br>\n" +
                "<div style='font-size:130%'>???????????? : \n" +
                "<strong>"+ requestDto.getCode() +"\n" +
                "</strong><div><br/></div>";

        msg.setText(body, "utf-8", "html");
        msg.setFrom(new InternetAddress("gunsong2@gmail.com", "?????? ??? ??? ??????"));
        return msg;
    }


    /*** ????????? ***/

    @Transactional
    @Override
    public AuthSignInResponseDto login(AuthSignInRequestDto requestDto) {


        // ????????? ????????? member ??????
        Members member = membersRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new AuthSignInException(ErrorCode.USER_NOT_FOUND));

        // member ????????? ?????? token??? ??????
        // authenticationManager?????? ??????
        // authenticate ?????????: DB??? ?????? ????????? ????????? ????????? ???????????? ??????
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // authentication??? jwtTokenProvider?????? ????????? jwt ????????? ??????
        // DB??? refresh token ??????
        // jwt ?????? ??????
        AuthTokenResponseDto token = jwtTokenProvider.createToken(authentication);
        AuthSignInResponseDto responseDto = AuthSignInResponseDto.builder().image(member.toByte(300, 300))
                .nickname(member.getNickname()).grantType(token.getGrantType()).accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken()).build();

        member.updateRefreshToken(token.getRefreshToken());
        return responseDto;
    }


    /*** ?????? ????????? ***/

    @Transactional
    @Override
    public AuthTokenResponseDto issueToken(HttpServletRequest request) {

        String accessToken = request.getHeader("ACCESS_TOKEN");
        String refreshToken = request.getHeader("REFRESH_TOKEN");

        // Refresh Token ?????? ??????
        if (jwtTokenProvider.validateToken(refreshToken) != JwtStatus.ACCESS) {
            System.out.println("???????????? ?????? ???????????? ???????????????");
            throw new AuthTokenException(ErrorCode.REFRESH_TOKEN_ERROR);
        }


        // Access Token?????? ?????? ?????? ??????
        // DB?????? ?????? ????????? Refresh Token??? ?????????
        // request??? ?????? ????????? ??????????????? ??????
        org.springframework.security.core.Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        Members member = membersRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthTokenException(ErrorCode.USER_NOT_FOUND));
        if (!member.getRefreshToken().equals(refreshToken)) {
            throw new AuthTokenException(ErrorCode.WRONG_USER);
        }

        // ?????????
        // DB??? ???????????? Refresh Token ????????????
        AuthTokenResponseDto responseDto = jwtTokenProvider.createToken(authentication);
        member.updateRefreshToken(responseDto.getRefreshToken());

        // jwt ?????? ??????
        return responseDto;
    }

    @Transactional
    private Authentication findAuthentication(String id) throws Exception {
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        ObjectMapper objectMapper = new ObjectMapper();
        Authentication data = objectMapper.convertValue(valueOperations.get(id), Authentication.class);
        if(data == null){
            throw new AuthSignUpException(ErrorCode.USER_NOT_FOUND);
        }
        return data;
    }
}
