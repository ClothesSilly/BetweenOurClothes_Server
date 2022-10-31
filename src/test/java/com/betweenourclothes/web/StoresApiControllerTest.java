package com.betweenourclothes.web;

import com.betweenourclothes.domain.clothes.repository.ClothesImageRepository;
import com.betweenourclothes.jwt.stores.Stores;
import com.betweenourclothes.jwt.stores.repository.StoresRepository;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoresApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StoresRepository storesRepository;

    @Autowired
    private ClothesImageRepository clothesImageRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Before
    public void cleanup(){
        clothesImageRepository.deleteAllInBatch();
        storesRepository.deleteAllInBatch();
    }

    private String AT;
    private String postId;

    @Test
    public void 로그인() throws Exception{
        String url_login = "http://localhost:" + port + "/api/v1/auth/login";
        String email = "gunsong2@naver.com";
        String pw = "abcde1234!";
        AuthSignInRequestDto reqDto = AuthSignInRequestDto.builder().email(email).password(pw).build();
        ResponseEntity<AuthTokenResponseDto> respDto = restTemplate.postForEntity(url_login, reqDto, AuthTokenResponseDto.class);
        assertThat(respDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        AT = respDto.getBody().getAccessToken();
    }

    @Test
    public void 중고거래_게시글등록() throws Exception{

        로그인();

        String token = "Bearer" + AT;
        System.out.println(token);

        String url_post = "http://localhost:" + port + "/api/v1/stores/post";

        // 게시글
        StoresPostRequestDto reqDto = StoresPostRequestDto.builder().style("스포티")
                .large_category("상의").small_category("탑").length("크롭").fit("타이트")
                .color("블랙").material("퍼").title("크롭 탑 판매합니다~").content("ㅈㄱㄴ").build();
        ObjectMapper mapper = new ObjectMapper();
        String dto2Json = mapper.writeValueAsString(reqDto);
        MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));

        // 사진
        MockMultipartFile file1 = new MockMultipartFile("image", "test.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));
        MockMultipartFile file2 = new MockMultipartFile("image", "test2.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test2.png"));
        MockMultipartFile file3 = new MockMultipartFile("image", "test3.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test3.png"));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(multipart(url_post).file(dtofile).file(file1).file(file2).file(file3).accept(MediaType.APPLICATION_JSON).header("Authorization", token)).andDo(print()).andExpect(status().isOk());


        List<Stores> stores = storesRepository.findAll();
        postId =  stores.get(0).getId().toString();
        //assertThat(closets.get(0).getAuthor().getEmail()).isEqualTo("gunsong2@naver.com");
        assertThat( stores.get(0).getStyle().getName()).isEqualTo("스포티");
        assertThat( stores.get(0).getClothesInfo().getCategoryL()).isEqualTo("상의");
        assertThat( stores.get(0).getClothesInfo().getCategoryS()).isEqualTo("탑");
        assertThat( stores.get(0).getClothesInfo().getLength()).isEqualTo("크롭");
        assertThat( stores.get(0).getClothesInfo().getFit()).isEqualTo("타이트");
        assertThat( stores.get(0).getColors().getName()).isEqualTo("블랙");
        assertThat( stores.get(0).getMaterials().getName()).isEqualTo("퍼");
        assertThat( stores.get(0).getTitle()).isEqualTo("크롭 탑 판매합니다~");
        assertThat( stores.get(0).getContent()).isEqualTo("ㅈㄱㄴ");

        /*assertThat(closets.get(0).getImages().size()).isEqualTo(3);
        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        assertThat(clothesImages.size()).isEqualTo(3);*/
    }


}
