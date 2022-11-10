package com.betweenourclothes.web;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.repository.ClothesImageRepository;
import com.betweenourclothes.domain.main.Recomm;
import com.betweenourclothes.domain.main.repository.RecommRepository;
import com.betweenourclothes.domain.members.repository.MembersLikeStoresPostRepository;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresCommentsRepository;
import com.betweenourclothes.domain.stores.repository.StoresRepository;

import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.main.MainRecommPostRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostClothesRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostSalesRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostSearchRequestDto;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;
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
public class MainApiControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ClosetsRepository closetsRepository;
    @Autowired
    private StoresRepository storesRepository;
    @Autowired
    private ClothesImageRepository clothesImageRepository;
    @Autowired
    private StoresCommentsRepository storesCommentsRepository;
    @Autowired
    private MembersLikeStoresPostRepository membersLikeStoresPostRepository;
    @Autowired
    private RecommRepository recommRepository;


    private String AT;
    private String postId;
    private String closets_postId;
    private List<StoresPostRequestDto> testData_post;
    private List<StoresPostClothesRequestDto> testData_clothes;
    private List<StoresPostSalesRequestDto> testData_sales;
    private List<MockMultipartFile> testData_img;

    private List<ClosetsPostRequestDto> testData;
    private List<MockMultipartFile> testData_img_closets;


    @Before
    public void 로그인_테스트데이터등록() throws Exception {
        로그인();
        내옷장_테스트데이터등록();
        중고거래_테스트데이터등록();
    }

    @After
    public void 추가한_게시글과이미지_지우기() throws Exception{
        List<ClothesImage> clothesImages = clothesImageRepository.findAll();
        for(ClothesImage image : clothesImages){
            File file = new File(image.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        clothesImageRepository.deleteAllInBatch();
        storesCommentsRepository.deleteAllInBatch();
        membersLikeStoresPostRepository.deleteAllInBatch();
        recommRepository.deleteAllInBatch();
        closetsRepository.deleteAllInBatch();
        storesRepository.deleteAllInBatch();
    }

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
    public void 메인_배너가져오기() throws Exception{

        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(get("/api/v1/main/banner").header("Authorization", token))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    public void 메인_추천등록가져오기수정가져오기() throws Exception{

        String token = "Bearer" + AT;
        String url = "http://localhost:" + port + "/api/v1/main/recomm/" + closets_postId;

        List<Long> arr = new ArrayList<>();
        arr.add(Long.parseLong(postId));
        arr.add(Long.parseLong(postId)-1);
        MainRecommPostRequestDto dto = MainRecommPostRequestDto.builder().stores_id(arr).build();

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<MainRecommPostRequestDto> req = new HttpEntity<>(dto, header);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, req, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        mockMvc.perform(get("/api/v1/main/recomm/" + closets_postId)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.[?(@.id=='%s')]", postId).exists())
                .andExpect(jsonPath("$.[?(@.id=='%s')]", Long.toString(Long.parseLong(postId)-1)).exists())
                .andReturn();

        arr = new ArrayList<>();
        arr.add(Long.parseLong(postId)-2);
        dto = MainRecommPostRequestDto.builder().stores_id(arr).build();

        String content = new ObjectMapper().writeValueAsString(dto);
        mockMvc.perform(patch("/api/v1/main/recomm/" + closets_postId)
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        mockMvc.perform(get("/api/v1/main/recomm/" + closets_postId)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.[?(@.id=='%s')]", postId).doesNotExist())
                .andExpect(jsonPath("$.[?(@.id=='%s')]", Long.toString(Long.parseLong(postId)-2)).exists())
                .andReturn();



    }

    @Test
    public void 내옷장_테스트데이터등록() throws Exception{

        내옷장_테스트데이터추가();
        String token = "Bearer" + AT;
        String url_post = "http://localhost:" + port + "/api/v1/closets/post";

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MockMultipartFile img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));

        for(int i=0; i<testData_img_closets.size(); i++){
            ObjectMapper mapper = new ObjectMapper();
            String dto2Json = mapper.writeValueAsString(testData.get(i));
            MockMultipartFile dtofile = new MockMultipartFile("data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));
            mockMvc.perform(multipart(url_post).file(dtofile).file(testData_img_closets.get(i)).file(img).accept(MediaType.APPLICATION_JSON).header("Authorization", token)).andExpect(status().isOk());
        }

        List<Closets> posts = closetsRepository.findAll();
        closets_postId = Long.toString(posts.get(posts.size()-1).getId());
    }

    @Test
    public void 내옷장_테스트데이터추가() throws Exception{


        testData = new ArrayList<>();
        testData_img_closets = new ArrayList<>();
        ClosetsPostRequestDto requestDto = null;
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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

    }

    @Test
    public void 중고거래_테스트데이터등록() throws Exception{
        중고거래_테스트데이터추가();
        String token = "Bearer" + AT;
        System.out.println(token);

        String url_post = "http://localhost:" + port + "/api/v1/stores/post";

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MockMultipartFile img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));

        for(int i=0; i<testData_img.size(); i++){
            ObjectMapper mapper = new ObjectMapper();
            String postdto2Json = mapper.writeValueAsString(testData_post.get(i));
            String clothesdto2Json = mapper.writeValueAsString(testData_clothes.get(i));
            String salesdto2Json = mapper.writeValueAsString(testData_sales.get(i));
            MockMultipartFile dtofile1 = new MockMultipartFile("clothes_data", "", "application/json", clothesdto2Json.getBytes(StandardCharsets.UTF_8));
            MockMultipartFile dtofile2 = new MockMultipartFile("sales_data", "", "application/json", salesdto2Json.getBytes(StandardCharsets.UTF_8));
            MockMultipartFile dtofile3 = new MockMultipartFile("post_data", "", "application/json", postdto2Json.getBytes(StandardCharsets.UTF_8));
            mockMvc.perform(multipart(url_post).file(dtofile1).file(dtofile2).file(dtofile3).file(testData_img.get(i)).file(img).accept(MediaType.APPLICATION_JSON).header("Authorization", token)).andExpect(status().isOk());
        }

        List<Stores> posts = storesRepository.findAll();
        postId = Long.toString(posts.get(posts.size()-1).getId());
    }

    @Test
    public void 중고거래_테스트데이터추가() throws Exception{


        testData_post = new ArrayList<>();
        testData_clothes = new ArrayList<>();
        testData_sales = new ArrayList<>();
        testData_img = new ArrayList<>();
        StoresPostClothesRequestDto clothesdto = null;
        StoresPostSalesRequestDto salesdto = null;
        StoresPostRequestDto postinfo = null;
        MockMultipartFile img = null;

        postinfo = StoresPostRequestDto.builder().title("하의").content("하의팔아요~").price("5000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("컨트리")
                .large_category("하의")
                .small_category("청바지")
                .fit("벨보텀")
                .length("미니")
                .color("카키")
                .material("스웨이드")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("브랜드")
                .clothes_color("화면보다 밝아요")
                .clothes_size("32")
                .clothes_gender("남")
                .status_score("4")
                .status_tag("있음")
                .status_times("3번 이하")
                .status_when2buy("1년 전")
                .user_size("2XL")
                .user_height("~155")
                .user_weight("45~50")
                .transport("택배")
                .clothes_length(36L)
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants2.jpg"));
        testData_img.add(img);


        postinfo = StoresPostRequestDto.builder().title("상의 블라우스").content("단돈 10000원에 팔아요~").price("10000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("레트로")
                .large_category("상의")
                .small_category("블라우스")
                .fit("타이트")
                .length("노멀")
                .color("블랙")
                .material("니트")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("보세")
                .clothes_color("화면과 같아요")
                .clothes_size("M")
                .clothes_gender("여")
                .status_score("2")
                .status_tag("없음")
                .status_times("")
                .status_when2buy("3~6개월 전")
                .user_size("M")
                .user_height("160~165")
                .user_weight("55~60")
                .transport("직거래")
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top.jpg"));
        testData_img.add(img);

        postinfo = StoresPostRequestDto.builder().title("니트").content("니트팔아요~").price("3000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("로맨틱")
                .large_category("상의")
                .small_category("후드티")
                .fit("루즈")
                .length("롱")
                .color("골드")
                .material("앙고라")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("브랜드")
                .clothes_color("화면보다 어두워요")
                .clothes_size("XS")
                .clothes_gender("남")
                .status_score("5")
                .status_tag("있음")
                .status_times("")
                .status_when2buy("~3개월 전")
                .user_size("S")
                .user_height("180~185")
                .user_weight("75~80")
                .transport("택배")
                .clothes_length(72L)
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top2.jpg"));
        testData_img.add(img);

        postinfo = StoresPostRequestDto.builder().title("청바지").content("청바지팔아요~").price("5000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("프레피")
                .large_category("하의")
                .small_category("청바지")
                .fit("스키니")
                .length("니렝스")
                .color("네온")
                .material("실크")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("보세")
                .clothes_color("")
                .clothes_size("XL")
                .clothes_gender("여")
                .status_score("4")
                .status_tag("없음")
                .status_times("3번 이하")
                .status_when2buy("3~6개월 전")
                .user_size("L")
                .user_height("175~180")
                .user_weight("65~70")
                .transport("직거래")
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants3.jpg"));
        testData_img.add(img);

        postinfo = StoresPostRequestDto.builder().title("원피스팝니다").content("ㅈㄱㄴ").price("1000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("아방가르드")
                .large_category("원피스")
                .small_category("점프수트")
                .fit("오버사이즈")
                .length("미니")
                .color("골드")
                .material("플리스")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("브랜드")
                .clothes_color("화면과 같아요")
                .clothes_size("2XL")
                .clothes_gender("남")
                .status_score("5")
                .status_tag("있음")
                .status_times("3번 이하")
                .status_when2buy("~3개월 전")
                .user_size("XL")
                .user_height("175~180")
                .user_weight("80~85")
                .transport("택배")
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/one-piece.jpg"));
        testData_img.add(img);

    }

}
