package com.betweenourclothes.web;

import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.repository.ClothesImageRepository;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresRepository;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.request.*;
import com.betweenourclothes.web.dto.response.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private List<StoresPostRequestDto> testData;
    private List<MockMultipartFile> testData_img;

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
    public void 중고거래_옷카테고리전부() throws Exception{
        중고거래_테스트데이터추가();
        중고거래_테스트데이터등록();
        String token = "Bearer" + AT;
        System.out.println(token);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        String data1 = "상의";
        String data2 = "블라우스";
        String data3 = "타이트";
        String color = "블랙";
        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder().color(color).nameL("상의").nameS("블라우스").fit(data3).build();
        //.nameL(data1).nameS(data2).build();

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        MvcResult result = mockMvc.perform(get("/api/v1/stores/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        StoresThumbnailsResponseDto resp = new ObjectMapper().readValue(json, StoresThumbnailsResponseDto.class);
        assertThat(resp.getImages().size()).isEqualTo(1);
        System.out.println(resp.getId().get(0));
    }


    @Test
    public void 중고거래_작은카테고리조회() throws Exception{

        중고거래_테스트데이터추가();
        중고거래_테스트데이터등록();
        String token = "Bearer" + AT;
        System.out.println(token);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        String data1 = "상의";
        String data2 = "블라우스";
        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder()
                .nameL(data1).nameS(data2).build();

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        MvcResult result = mockMvc.perform(get("/api/v1/stores/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        StoresThumbnailsResponseDto resp = new ObjectMapper().readValue(json, StoresThumbnailsResponseDto.class);
        assertThat(resp.getImages().size()).isEqualTo(1);


        data1 = "하의";
        data2 = "청바지";

        req = StoresSearchCategoryAllRequestDto.builder().nameL(data1).nameS(data2).build();

        mapper = new ObjectMapper();
        content = mapper.writeValueAsString(req);

        result = mockMvc.perform(get("/api/v1/stores/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        json = result.getResponse().getContentAsString();
        resp = new ObjectMapper().readValue(json, StoresThumbnailsResponseDto.class);
        assertThat(resp.getImages().size()).isEqualTo(2);

    }

    @Test
    public void 중고거래_큰카테고리조회() throws Exception{
        중고거래_테스트데이터추가();
        중고거래_테스트데이터등록();
        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder().nameL("상의").build();
        String data2json = new ObjectMapper().writeValueAsString(req);
        System.out.println(data2json);
        MvcResult result = mockMvc.perform(get("/api/v1/stores/post/category?page=0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data2json)
                .header("Authorization", token)).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        StoresThumbnailsResponseDto resp = new ObjectMapper().readValue(json, StoresThumbnailsResponseDto.class);
        System.out.println(resp.getImages().get(0));
        System.out.println(resp.getId().get(0));
    }

    @Test
    public void 중고거래_테스트데이터등록() throws Exception{

        로그인();

        String token = "Bearer" + AT;
        System.out.println(token);

        String url_post = "http://localhost:" + port + "/api/v1/stores/post";

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MockMultipartFile img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));

        for(int i=0; i<testData_img.size(); i++){
            ObjectMapper mapper = new ObjectMapper();
            String dto2Json = mapper.writeValueAsString(testData.get(i));
            MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));
            mockMvc.perform(multipart(url_post).file(dtofile).file(testData_img.get(i)).file(img).accept(MediaType.APPLICATION_JSON).header("Authorization", token)).andExpect(status().isOk());
        }

    }

    @Test
    public void 중고거래_테스트데이터추가() throws Exception{


        testData = new ArrayList<>();
        testData_img = new ArrayList<>();
        StoresPostRequestDto requestDto = null;
        MockMultipartFile img = null;


        requestDto = requestDto.builder()
                .style("컨트리")
                .large_category("하의")
                .small_category("청바지")
                .fit("벨보텀")
                .length("미니")
                .color("카키")
                .material("스웨이드")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants2.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("레트로")
                .large_category("상의")
                .small_category("블라우스")
                .fit("타이트")
                .length("노멀")
                .color("블랙")
                .material("니트")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("로맨틱")
                .large_category("상의")
                .small_category("후드티")
                .fit("루즈")
                .length("롱")
                .color("골드")
                .material("앙고라")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top2.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("프레피")
                .large_category("하의")
                .small_category("청바지")
                .fit("스키니")
                .length("니렝스")
                .color("네온")
                .material("실크")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants3.jpg"));
        testData_img.add(img);

        requestDto = requestDto.builder()
                .style("아방가르드")
                .large_category("원피스")
                .small_category("점프수트")
                .fit("오버사이즈")
                .length("미니")
                .color("골드")
                .material("플리스")
                .build();
        testData.add(requestDto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/one-piece.jpg"));
        testData_img.add(img);

    }

    @Test
    public void 중고거래_썸네일불러오기() throws Exception{

        로그인();
        중고거래_게시글등록();
        중고거래_게시글등록();
        중고거래_게시글등록();
        중고거래_게시글등록();

        String token = "Bearer" + AT;

        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder().build();
        String data2json = new ObjectMapper().writeValueAsString(req);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MvcResult result = mockMvc.perform(get("/api/v1/stores/post/category?page=0").header("Authorization", token)
                        .content(data2json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        StoresThumbnailsResponseDto resp = new ObjectMapper().readValue(json, StoresThumbnailsResponseDto.class);

        assertThat(resp.getImages().size()).isEqualTo(4);
        System.out.println(resp.getId().get(0));
        System.out.println(resp.getId().get(1));
        System.out.println(resp.getId().get(2));
        System.out.println(resp.getId().get(3));
    }

    @Test
    public void 중고거래_게시글id로불러오기() throws Exception{
        로그인();
        중고거래_게시글등록();

        String token = "Bearer" + AT;
        String url_get = "http://localhost:" + port + "/api/v1/stores/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<StoresImagesResponseDto> resp
                = restTemplate.exchange(url_get, HttpMethod.GET, req, StoresImagesResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(Long.parseLong(postId));
        assertThat(resp.getBody().getImages().size()).isEqualTo(3);
    }

    @Test
    public void 중고거래_게시글삭제() throws Exception{
        로그인();
        중고거래_게시글등록();

        String token = "Bearer" + AT;
        String url_delete = "http://localhost:" + port + "/api/v1/stores/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(Long.parseLong(postId), header);

        ResponseEntity<String> resp = restTemplate.exchange(url_delete, HttpMethod.DELETE, req, String.class);
        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Stores> all = storesRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        assertThat(clothesImages.size()).isEqualTo(0);
    }

    @Test
    public void 중고거래_게시글수정() throws Exception{
        로그인();
        중고거래_게시글등록();

        String token = "Bearer" + AT;
        String url_patch = "http://localhost:" + port + "/api/v1/stores/post/" + postId;

        // 게시글
        StoresPostRequestDto reqDto = StoresPostRequestDto.builder().style("레트로")
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


        List<Stores> stores = storesRepository.findAll();
        postId = stores.get(0).getId().toString();
        Stores last = stores.get(stores.size()-1);
        assertThat(last.getStyle().getName()).isEqualTo("레트로");
        assertThat(last.getClothesInfo().getCategoryL()).isEqualTo("원피스");
        assertThat(last.getClothesInfo().getCategoryS()).isEqualTo("점프수트");
        assertThat(last.getClothesInfo().getLength()).isEqualTo("맥시");
        assertThat(last.getClothesInfo().getFit()).isEqualTo("루즈");
        assertThat(last.getColors().getName()).isEqualTo("레드");
        assertThat(last.getMaterials().getName()).isEqualTo("니트");
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
        //assertThat(.get(0).getAuthor().getEmail()).isEqualTo("gunsong2@naver.com");
        assertThat( stores.get(0).getStyle().getName()).isEqualTo("스포티");
        assertThat( stores.get(0).getClothesInfo().getCategoryL()).isEqualTo("상의");
        assertThat( stores.get(0).getClothesInfo().getCategoryS()).isEqualTo("탑");
        assertThat( stores.get(0).getClothesInfo().getLength()).isEqualTo("크롭");
        assertThat( stores.get(0).getClothesInfo().getFit()).isEqualTo("타이트");
        assertThat( stores.get(0).getColors().getName()).isEqualTo("블랙");
        assertThat( stores.get(0).getMaterials().getName()).isEqualTo("퍼");
        assertThat( stores.get(0).getTitle()).isEqualTo("크롭 탑 판매합니다~");
        assertThat( stores.get(0).getContent()).isEqualTo("ㅈㄱㄴ");

    }


}
