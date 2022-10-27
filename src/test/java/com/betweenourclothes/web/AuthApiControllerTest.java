package com.betweenourclothes.web;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.web.dto.request.AuthEmailRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.AuthSignUpRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.domain.auth.Email;
import com.betweenourclothes.domain.auth.EmailRepository;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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


    @Before
    public void cleanup(){
        membersRepository.deleteAll();
        emailRepository.deleteAll();
    }


    @Test
    public void 로그인_토큰재발급() throws Exception {
        //회원가입();

        String url_login = "http://localhost:" + port + "api/v1/auth/login";
        String email = "gunsong2@naver.com";
        String pw = "abcde1234!";
        AuthSignInRequestDto reqDto = AuthSignInRequestDto.builder().email(email).password(pw).build();

        ResponseEntity<AuthTokenResponseDto> respDto = restTemplate.postForEntity(url_login, reqDto, AuthTokenResponseDto.class);

        assertThat(respDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respDto.getBody()).isNotNull();

        String grantType = respDto.getBody().getGrantType();
        String accessToken = respDto.getBody().getAccessToken();
        String refreshToken = respDto.getBody().getRefreshToken();

        Thread.sleep(1000 * 5);
        String url_issue = "http://localhost:" + port + "api/v1/auth/issue";
        HttpHeaders headers = new HttpHeaders();
        headers.set("ACCESS_TOKEN", accessToken);
        headers.set("REFRESH_TOKEN", refreshToken);
        HttpEntity<ClosetsPostRequestDto> request = new HttpEntity<>(headers);
        ResponseEntity<AuthTokenResponseDto> respDto2 = restTemplate.postForEntity(url_issue, request, AuthTokenResponseDto.class);

        assertThat(respDto2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respDto2.getBody().getAccessToken()).isNotEqualTo(accessToken);
        assertThat(respDto2.getBody().getRefreshToken()).isNotEqualTo(refreshToken);
    }

    @Test
    public void 로그인_토큰전송() throws Exception{
        //회원가입();

        String url = "http://localhost:" + port + "api/v1/auth/login";
        String email = "gunsong2@naver.com";
        String pw = "abcde1234!";
        AuthSignInRequestDto reqDto = AuthSignInRequestDto.builder().email(email).password(pw).build();

        ResponseEntity<AuthTokenResponseDto> respDto = restTemplate.postForEntity(url, reqDto, AuthTokenResponseDto.class);

        assertThat(respDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respDto.getBody()).isNotNull();

        System.out.println(respDto.getBody().getGrantType());
        System.out.println(respDto.getBody().getAccessToken());
        System.out.println(respDto.getBody().getRefreshToken());

        List<Members> all = membersRepository.findAll();
        assertThat(all.get(all.size()-1).getRefreshToken()).isEqualTo(respDto.getBody().getRefreshToken());
    }
    @Test
    public void 회원가입() throws Exception{

        String url_email = "http://localhost:" + port + "api/v1/auth/sign-up/email";
        String url_code = "http://localhost:" + port + "api/v1/auth/sign-up/code";
        String url_signup = "http://localhost:" + port + "api/v1/auth/sign-up";

        String email = "gunsong2@naver.com";
        AuthEmailRequestDto dto1 = AuthEmailRequestDto.builder().email(email).build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url_email, dto1, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        responseEntity = restTemplate.postForEntity(url_code, dto1, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Email> all = emailRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
        assertThat(all.get(all.size()-1).getStatus()).isEqualTo("Y");

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        String password = "abcde1234!";
        String name = "이름";
        String nickname = "송아";
        String phone = "00033334444";

        AuthSignUpRequestDto dto2 = AuthSignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .build();

        MockMultipartFile file = new MockMultipartFile("image", "test.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));

        ObjectMapper mapper = new ObjectMapper();
        String dto2Json = mapper.writeValueAsString(dto2);
        System.out.println(dto2Json);
        MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart(url_signup).file(dtofile).file(file).accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());

        List<Members> mems = membersRepository.findAll();
        assertThat(mems.get(mems.size()-1).getEmail()).isEqualTo(email);
        System.out.println(mems.get(mems.size()-1).getImage());

        all = emailRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }


    @Test
    public void 이메일_중복체크() throws Exception{

        회원가입();

        String url_email = "http://localhost:" + port + "api/v1/auth/sign-up/email";
        String email = "gunsong2@naver.com";
        AuthEmailRequestDto dto1 = AuthEmailRequestDto.builder().email(email).build();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url_email, dto1, String.class);

        System.out.println(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

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
