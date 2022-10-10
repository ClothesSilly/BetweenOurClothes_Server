package com.betweenourclothes.web;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.domain.members.Role;
import com.betweenourclothes.web.dto.AuthSignUpRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.betweenourclothes.domain.members.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MembersRepository membersRepository;

    @After
    public void cleanup(){
        membersRepository.deleteAll();
    }

    @Test
    public void 회원가입() throws Exception{
        String email = "d126@naver.com";
        String password = "qwer1234!";
        String name = "이름2";
        String nickname = "송아";
        String phone = "00033334444";
        Role role = USER;

        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).role(role).build();

        String url = "http://localhost:" + port + "api/v1/auth/sign-up";

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, requestDto, Object.class);
        System.out.println(responseEntity.getBody().toString());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Members> all = membersRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
    }

    @Test
    public void 이메일_중복체크() throws Exception{
        String email = "d126@naver.com";
        String password = "qwer1234!";
        String name = "이름2";
        String nickname = "닉넴";
        String phone = "00033334444";
        Role role = USER;

        String url = "http://localhost:" + port + "api/v1/auth/sign-up";
        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).role(role).build();

        ResponseEntity<Object> responseEntity1 = restTemplate.postForEntity(url, requestDto, Object.class);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Object> responseEntity2 = restTemplate.postForEntity(url, requestDto, Object.class);
        System.out.println(responseEntity2.getBody().toString());
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void 닉네임_중복체크() throws Exception{
        String email = "d126@naver.com";
        String password = "qwer1234!";
        String name = "이름2";
        String nickname = "닉넴";
        String phone = "00033334444";
        Role role = USER;

        String url = "http://localhost:" + port + "api/v1/auth/sign-up";
        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).role(role).build();

        ResponseEntity<Object> responseEntity1 = restTemplate.postForEntity(url, requestDto, Object.class);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);

        String email2 = "sdfsdfsd@naver.com";
        requestDto = AuthSignUpRequestDto.builder().email(email2).password(password).name(name).nickname(nickname).phone(phone).role(role).build();
        ResponseEntity<Object> responseEntity2 = restTemplate.postForEntity(url, requestDto, Object.class);
        System.out.println(responseEntity2.getBody().toString());
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void 비밀번호_형식체크() throws Exception{
        String email = "d126@naver.com";
        String password = "qwer";
        String name = "이름2";
        String nickname = "닉넴";
        String phone = "00033334444";
        Role role = USER;

        String url = "http://localhost:" + port + "api/v1/auth/sign-up";
        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).role(role).build();

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, requestDto, Object.class);
        System.out.println(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 이메일_형식체크() throws Exception{
        String email = "d1dfs26naver.cㄴㅇㅇㄴㄹㅇㄴㄹom";
        String password = "Qwer1234!";
        String name = "이름2";
        String nickname = "닉넴";
        String phone = "00033334444";
        Role role = USER;

        String url = "http://localhost:" + port + "api/v1/auth/sign-up";
        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).role(role).build();

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, requestDto, Object.class);
        System.out.println(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
