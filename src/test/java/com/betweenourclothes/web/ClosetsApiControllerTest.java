package com.betweenourclothes.web;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.ClosetsRepository;
import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClosetsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClosetsRepository closetsRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @After
    public void cleanup(){
        closetsRepository.deleteAll();
    }

    private String AT ;
    @Test
    public void 로그인() throws Exception{
        String url_login = "http://localhost:" + port + "/api/v1/auth/login";
        String email = "gunsong2@naver.com";
        String pw = "qwer1234!";
        AuthSignInRequestDto reqDto = AuthSignInRequestDto.builder().email(email).password(pw).build();
        ResponseEntity<AuthTokenResponseDto> respDto = restTemplate.postForEntity(url_login, reqDto, AuthTokenResponseDto.class);
        AT = respDto.getBody().getAccessToken();
    }

    @Test
    public void 내옷장_게시글등록() throws Exception{
        String url_post = "http://localhost:" + port + "/api/v1/closets/post";
        ClosetsPostRequestDto reqDto = ClosetsPostRequestDto.builder().content("게시글게시글게시글우와아아아아").style("스포티").build();

        로그인();
        String token = "Bearer" + AT;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<ClosetsPostRequestDto> request = new HttpEntity<>(reqDto, headers);

        ResponseEntity<String> respDto = restTemplate.postForEntity(url_post, request, String.class);
        assertThat(respDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respDto.getBody()).isEqualTo("등록 완료");

        List<Closets> closets = closetsRepository.findAll();
        assertThat(closets.get(0).getAuthor().getEmail()).isEqualTo("gunsong2@naver.com");
        assertThat(closets.get(0).getContent()).isEqualTo("게시글게시글게시글우와아아아아");
        assertThat(closets.get(0).getStyle().getName()).isEqualTo("스포티");
    }

    @Test
    public void 내옷장_사진추가() throws Exception{

        로그인();
        String token = "Bearer" + AT;

        MockMultipartFile file1 = new MockMultipartFile("image", "test.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));
        MockMultipartFile file2 = new MockMultipartFile("image", "test2.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test2.png"));
        MockMultipartFile file3 = new MockMultipartFile("image", "test3.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test3.png"));


        String url = "http://localhost:" + port + "/api/v1/closets/post/images";


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart(url).file(file1).file(file2).file(file3).header("Authorization",token)).andExpect(status().isOk()).andDo(print());


    }

}
