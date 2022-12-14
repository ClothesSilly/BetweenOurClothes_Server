package com.betweenourclothes.web;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.repository.ClothesImageRepository;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.auth.AuthSignInResponseDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
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

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    public void ?????????_????????????????????????() throws Exception {
        ?????????();
        ?????????_????????????????????????();
    }

    @After
    public void ?????????_?????????????????????_?????????() throws Exception{
        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        for(ClothesImage image : clothesImages){
            File file = new File(image.getPath());
            if(file.exists()){
                file.delete();
            }
        }
        clothesImageRepository.deleteAllInBatch();
        closetsRepository.deleteAllInBatch();
    }

    private String AT;
    private String postId;

    private List<ClosetsPostRequestDto> testData;
    private List<MockMultipartFile> testData_img;

    @Test
    public void ?????????() throws Exception{
        String url_login = "http://localhost:" + port + "/api/v1/auth/login";
        String email = "gunsong2@naver.com";
        String pw = "abcde1234!";
        AuthSignInRequestDto reqDto = AuthSignInRequestDto.builder().email(email).password(pw).build();
        ResponseEntity<AuthSignInResponseDto> respDto = restTemplate.postForEntity(url_login, reqDto, AuthSignInResponseDto.class);
        assertThat(respDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respDto.getBody().getNickname()).isEqualTo("??????");
        AT = respDto.getBody().getAccessToken();
    }

    @Test
    public void ?????????_??????????????????() throws Exception{

        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        String data1 = "??????";
        String data2 = "????????????";
        String data3 = "?????????";
        String color = "??????";
        ClosetsSearchCategoryAllRequestDto req = ClosetsSearchCategoryAllRequestDto.builder().color(color).nameL("??????").nameS("????????????").fit(data3).build();

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        MvcResult result = mockMvc.perform(post("/api/v1/closets/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(1)))
                .andReturn();
    }

    @Test
    public void ?????????_????????????????????????() throws Exception{

        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        String data1 = "??????";
        String data2 = "????????????";
        ClosetsSearchCategoryAllRequestDto req = ClosetsSearchCategoryAllRequestDto.builder()
                .nameL(data1).nameS(data2).build();

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        MvcResult result = mockMvc.perform(post("/api/v1/closets/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(1)))
                .andReturn();


        data1 = "??????";
        data2 = "?????????";

        req = ClosetsSearchCategoryAllRequestDto.builder().nameL(data1).nameS(data2).build();

        mapper = new ObjectMapper();
        content = mapper.writeValueAsString(req);

        result = mockMvc.perform(post("/api/v1/closets/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content).header("Authorization", token))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(2)))
                .andReturn();
    }

    @Test
    public void ?????????_?????????????????????() throws Exception{

        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        ClosetsSearchCategoryAllRequestDto req = ClosetsSearchCategoryAllRequestDto.builder().nameL("??????").build();
        String data2json = new ObjectMapper().writeValueAsString(req);
        System.out.println(data2json);
        MvcResult result = mockMvc.perform(post("/api/v1/closets/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data2json)
                        .header("Authorization", token)).andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(2)))
                .andReturn();
    }

    @Test
    public void ?????????_????????????????????????() throws Exception{

        ?????????_????????????????????????();
        String token = "Bearer" + AT;
        String url_post = "http://localhost:" + port + "/api/v1/closets/post";

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MockMultipartFile img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));

        for(int i=0; i<testData_img.size(); i++){
            ObjectMapper mapper = new ObjectMapper();
            String dto2Json = mapper.writeValueAsString(testData.get(i));
            MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));
            mockMvc.perform(multipart(url_post).file(dtofile).file(testData_img.get(i)).file(img)
                    .accept(MediaType.APPLICATION_JSON).header("Authorization", token)).andExpect(status().isOk());
        }

        List<Closets> posts = closetsRepository.findAll();
        postId = Long.toString(posts.get(posts.size()-1).getId());
    }

    @Test
    public void ?????????_????????????????????????() throws Exception{


        testData = new ArrayList<>();
        testData_img = new ArrayList<>();
        ClosetsPostRequestDto requestDto = null;
        MockMultipartFile img = null;


        requestDto = requestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("?????????")
                .length("??????")
                .color("??????")
                .material("????????????")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants2.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("????????????")
                .fit("?????????")
                .length("??????")
                .color("??????")
                .material("??????")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("??????")
                .length("???")
                .color("??????")
                .material("?????????")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top2.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("?????????")
                .length("?????????")
                .color("??????")
                .material("??????")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants3.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("???????????????")
                .large_category("?????????")
                .small_category("????????????")
                .fit("???????????????")
                .length("??????")
                .color("??????")
                .material("?????????")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/one-piece.jpg"));
        testData_img.add(img);

    }

    @Test
    public void ?????????_?????????ID???????????????() throws Exception{

        String token = "Bearer" + AT;
        String url_get = "http://localhost:" + port + "/api/v1/closets/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<ClosetsImagesResponseDto> resp
                = restTemplate.exchange(url_get, HttpMethod.GET, req, ClosetsImagesResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(Long.parseLong(postId));
        assertThat(resp.getBody().getImages().size()).isEqualTo(2);
    }

    @Test
    public void ?????????_?????????????????????() throws Exception{

        String token = "Bearer" + AT;

        ClosetsSearchCategoryAllRequestDto req = ClosetsSearchCategoryAllRequestDto.builder().build();
        String data2json = new ObjectMapper().writeValueAsString(req);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MvcResult result = mockMvc.perform(post("/api/v1/closets/post/category?page=0").header("Authorization", token)
                        .content(data2json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andReturn();
                //.andExpect(jsonPath("$.content", hasSize(5))).andReturn();
    }

    @Test
    public void ?????????_png_jpg_?????????_??????_????????????() throws Exception{
        MockMultipartFile img1 =  new MockMultipartFile("image", "test.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));
        MockMultipartFile img2 =  new MockMultipartFile("image", "pants2.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/quality.jpg"));

        ClosetsPostRequestDto requestDto = ClosetsPostRequestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("?????????")
                .length("??????")
                .color("??????")
                .material("????????????")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String dto2Json = mapper.writeValueAsString(requestDto);
        MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        String token = "Bearer" + AT;
        String url_post = "http://localhost:" + port + "/api/v1/closets/post";

        mockMvc.perform(multipart(url_post).file(dtofile).file(img1).file(img2)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", token)).andExpect(status().isOk());

        List<Closets> posts = closetsRepository.findAll();
        postId = Long.toString(posts.get(posts.size()-1).getId());

        String url_get = "http://localhost:" + port + "/api/v1/closets/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<ClosetsImagesResponseDto> resp
                = restTemplate.exchange(url_get, HttpMethod.GET, req, ClosetsImagesResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(Long.parseLong(postId));
        assertThat(resp.getBody().getImages().size()).isEqualTo(2);
    }
    @Test
    public void ?????????_???????????????() throws Exception{

        String token = "Bearer" + AT;
        String url_delete = "http://localhost:" + port + "/api/v1/closets/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<String> resp = restTemplate.exchange(url_delete, HttpMethod.DELETE, req, String.class);
        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void ?????????_???????????????() throws Exception{
        String token = "Bearer" + AT;
        String url_patch = "http://localhost:" + port + "/api/v1/closets/post/" + postId;

        // ?????????
        ClosetsPostRequestDto reqDto = ClosetsPostRequestDto.builder().style("?????????")
                .large_category("?????????").small_category("????????????").length("??????").fit("??????")
                .color("??????").material("??????").build();

        ObjectMapper mapper = new ObjectMapper();
        String dto2Json = mapper.writeValueAsString(reqDto);
        MockPart id = new MockPart("id", postId.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));

        // ??????
        MockMultipartFile file1 = new MockMultipartFile("image", "test4.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));
        MockMultipartFile file2 = new MockMultipartFile("image", "test4.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(multipart(url_patch).part(id).file(dtofile).file(file1).file(file2).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                        .with(r -> { r.setMethod("PUT"); return r; }))
                .andDo(print()).andExpect(status().isOk());

        List<Closets> closets = closetsRepository.findAll();
        Closets last = closets.get(closets.size()-1);
        assertThat(last.getStyle().getName()).isEqualTo("?????????");
        assertThat(last.getClothesInfo().getCategoryL()).isEqualTo("?????????");
        assertThat(last.getClothesInfo().getCategoryS()).isEqualTo("????????????");
        assertThat(last.getClothesInfo().getLength()).isEqualTo("??????");
        assertThat(last.getClothesInfo().getFit()).isEqualTo("??????");
        assertThat(last.getColors().getName()).isEqualTo("??????");
        assertThat(last.getMaterials().getName()).isEqualTo("??????");
    }



}
