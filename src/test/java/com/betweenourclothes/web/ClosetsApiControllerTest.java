package com.betweenourclothes.web;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.ClosetsRepository;
import com.betweenourclothes.domain.clothes.ClothesImageRepository;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.request.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.response.AuthTokenResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
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
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ClothesImageRepository clothesImageRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Before
    public void cleanup(){
        clothesImageRepository.deleteAllInBatch();
        closetsRepository.deleteAllInBatch();
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
    public void 내옷장_썸네일페이징() throws Exception{

        로그인();
        for(int i=0; i<20; i++){
            내옷장_게시글등록();
        }

        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MvcResult result = mockMvc.perform(get("/api/v1/closets/post/thumbnails?page=0").header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ClosetsThumbnailsResponseDto resp = new ObjectMapper().readValue(json, ClosetsThumbnailsResponseDto.class);
        assertThat(resp.getImages().size()).isEqualTo(18);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        result = mockMvc.perform(get("/api/v1/closets/post/thumbnails?page=1").header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        json = result.getResponse().getContentAsString();
        resp = new ObjectMapper().readValue(json, ClosetsThumbnailsResponseDto.class);
        assertThat(resp.getImages().size()).isEqualTo(2);
    }
    @Test
    public void 내옷장_게시글ID로불러오기() throws Exception{

        로그인();
        내옷장_게시글등록();

        String token = "Bearer" + AT;
        String url_get = "http://localhost:" + port + "/api/v1/closets/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<ClosetsImagesResponseDto> resp
                = restTemplate.exchange(url_get, HttpMethod.GET, req, ClosetsImagesResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(postId);
        assertThat(resp.getBody().getImages().size()).isEqualTo(3);
    }

    @Test
    public void 내옷장_썸네일불러오기() throws Exception{

        로그인();
        내옷장_게시글등록();
        내옷장_게시글등록();
        내옷장_게시글등록();
        내옷장_게시글등록();

        String token = "Bearer" + AT;
        String url_get = "http://localhost:" + port + "/api/v1/closets/post/thumbnails";

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(header);

        ResponseEntity<ClosetsThumbnailsResponseDto> resp
         = restTemplate.exchange(url_get, HttpMethod.GET, req, ClosetsThumbnailsResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getImages().size()).isEqualTo(4);

    }


    //@Transactional
    @Test
    public void 내옷장_게시글삭제() throws Exception{
        로그인();
        내옷장_게시글등록();

        String token = "Bearer" + AT;
        String url_delete = "http://localhost:" + port + "/api/v1/closets/post/" + postId;
        //System.out.println(postId);

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<String> resp = restTemplate.exchange(url_delete, HttpMethod.DELETE, req, String.class);
        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        /*List<Closets> all = closetsRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        assertThat(clothesImages.size()).isEqualTo(0);*/
    }

    //@Transactional
    @Test
    public void 내옷장_게시글수정() throws Exception{
        로그인();
        내옷장_게시글등록();

        String token = "Bearer" + AT;
        String url_patch = "http://localhost:" + port + "/api/v1/closets/post/" + postId;

        // 게시글
        ClosetsPostRequestDto reqDto = ClosetsPostRequestDto.builder().style("레트로")
                .large_category("원피스").small_category("점프수트").length("맥시").fit("루즈")
                .color("레드").material("니트").build();
        ObjectMapper mapper = new ObjectMapper();
        String dto2Json = mapper.writeValueAsString(reqDto);
        MockPart id = new MockPart("id", postId.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));

        // 사진
        MockMultipartFile file1 = new MockMultipartFile("image", "test4.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test4.png"));
        MockMultipartFile file2 = new MockMultipartFile("image", "test4.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test4.png"));


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(multipart(url_patch).part(id).file(dtofile).file(file1).file(file2).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                        .with(r -> { r.setMethod("PATCH"); return r; }))
                .andDo(print()).andExpect(status().isOk());

        /*List<Closets> closets = closetsRepository.findAll();
        assertThat(closets.get(0).getAuthor().getEmail()).isEqualTo("gunsong2@naver.com");
        assertThat(closets.get(0).getStyle().getName()).isEqualTo("레트로");
        assertThat(closets.get(0).getImages().size()).isEqualTo(2);

        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        assertThat(clothesImages.size()).isEqualTo(2);*/

        List<Closets> closets = closetsRepository.findAll();
        postId = closets.get(0).getId().toString();
        Closets last = closets.get(closets.size()-1);
        //assertThat(closets.get(0).getAuthor().getEmail()).isEqualTo("gunsong2@naver.com");
        assertThat(last.getStyle().getName()).isEqualTo("레트로");
        assertThat(last.getClothesInfo().getCategoryL()).isEqualTo("원피스");
        assertThat(last.getClothesInfo().getCategoryS()).isEqualTo("점프수트");
        assertThat(last.getClothesInfo().getLength()).isEqualTo("맥시");
        assertThat(last.getClothesInfo().getFit()).isEqualTo("루즈");
        assertThat(last.getColors().getName()).isEqualTo("레드");
        assertThat(last.getMaterials().getName()).isEqualTo("니트");
    }

    //@Transactional
    @Test
    public void 내옷장_게시글등록() throws Exception{

        로그인();

        String token = "Bearer" + AT;
        System.out.println(token);

        String url_post = "http://localhost:" + port + "/api/v1/closets/post";

        // 게시글
        ClosetsPostRequestDto reqDto = ClosetsPostRequestDto.builder().style("스포티")
                .large_category("상의").small_category("탑").length("크롭").fit("타이트")
                .color("블랙").material("퍼").build();
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


        List<Closets> closets = closetsRepository.findAll();
        postId = closets.get(0).getId().toString();
        //assertThat(closets.get(0).getAuthor().getEmail()).isEqualTo("gunsong2@naver.com");
        assertThat(closets.get(0).getStyle().getName()).isEqualTo("스포티");
        assertThat(closets.get(0).getClothesInfo().getCategoryL()).isEqualTo("상의");
        assertThat(closets.get(0).getClothesInfo().getCategoryS()).isEqualTo("탑");
        assertThat(closets.get(0).getClothesInfo().getLength()).isEqualTo("크롭");
        assertThat(closets.get(0).getClothesInfo().getFit()).isEqualTo("타이트");
        assertThat(closets.get(0).getColors().getName()).isEqualTo("블랙");
        assertThat(closets.get(0).getMaterials().getName()).isEqualTo("퍼");

        /*assertThat(closets.get(0).getImages().size()).isEqualTo(3);
        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        assertThat(clothesImages.size()).isEqualTo(3);*/
    }

    @Test
    public void 내옷장_테스트() throws Exception{
        로그인();
        String token = "Bearer" + AT;
        System.out.println(token);
        //jwtTokenProvider.getAuthentication(AT).getAuthorities().stream().map(e->e.getAuthority()).forEach(System.out::println);

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);

        HttpEntity<ClosetsPostRequestDto> req = new HttpEntity<>(header);

        String url = "http://localhost:" + port + "/api/v1/closets/test";
        ResponseEntity<String> respDto = restTemplate.postForEntity(url, req, String.class);

        assertThat(respDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(respDto.getBody());
    }
}
