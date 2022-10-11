package com.betweenourclothes.web;

import com.betweenourclothes.domain.members.*;
import com.betweenourclothes.web.dto.AuthEmailRequestDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.util.List;

import static com.betweenourclothes.domain.members.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @After
    public void cleanup(){
        membersRepository.deleteAll();
        emailRepository.deleteAll();
    }

    @Test
    public void 회원가입() throws Exception{
        String email = "d16@naver.com";
        String password = "qwer1234!";
        String name = "이름2";
        String nickname = "송아아";
        String phone = "00033334444";
        String image = "C:/Users/user1/Desktop/송아/캡스톤/repo/between-our-clothes-server/src/main/resources/static/images/profile/profile-ce50a348-62ce-4b6a-883f-633a618f1547.png";

        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).image(image).build();

        String url = "http://localhost:" + port + "api/v1/auth/sign-up";

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, requestDto, Object.class);
        System.out.println(responseEntity.getBody().toString());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Members> all = membersRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
        assertThat(all.get(all.size()-1).getImage()).isEqualTo(image);
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

    @Test
    public void 프로필이미지_업로드() throws Exception{

        MockMultipartFile file = new MockMultipartFile("image", "test.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));

        String url = "http://localhost:" + port + "api/v1/auth/sign-up/image";

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart(url).file(file)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void 이메일_전송() throws Exception{
        String url = "http://localhost:" + port + "api/v1/auth/sign-up/email";
        String email = "gunsong2@naver.com";
        AuthEmailRequestDto requestDto = AuthEmailRequestDto.builder().email(email).build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        System.out.println(responseEntity.getBody().toString());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Email> all = emailRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
        System.out.println(all.get(all.size()-1).getCode());
        System.out.println(all.get(all.size()-1).getStatus());
    }

    @Test
    public void 이메일_전송후_인증() throws Exception{

        String url = "http://localhost:" + port + "api/v1/auth/sign-up/email";
        String email = "gunsong2@naver.com";
        AuthEmailRequestDto requestDto = AuthEmailRequestDto.builder().email(email).build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Email> all = emailRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
        assertThat(all.get(all.size()-1).getStatus()).isEqualTo("N");

        url = "http://localhost:" + port + "api/v1/auth/sign-up/code";
        String authcode = requestDto.getCode();
        responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        all = emailRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
        assertThat(all.get(all.size()-1).getCode()).isEqualTo(authcode);
        assertThat(all.get(all.size()-1).getStatus()).isEqualTo("Y");
    }
}
