package com.betweenourclothes.web;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.repository.ClothesImageRepository;
import com.betweenourclothes.domain.members.repository.MembersLikeStoresPostRepository;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresCommentsRepository;
import com.betweenourclothes.domain.stores.repository.StoresRepository;

import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostClothesRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostSalesRequestDto;
import com.betweenourclothes.web.dto.request.stores.StoresPostSearchRequestDto;
import com.betweenourclothes.web.dto.response.auth.AuthSignInResponseDto;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    public void ?????????_????????????????????????() throws Exception {
        ?????????();
        ?????????_????????????????????????();
        ????????????_????????????????????????();
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
        storesCommentsRepository.deleteAllInBatch();
        membersLikeStoresPostRepository.deleteAllInBatch();
        closetsRepository.deleteAllInBatch();
        storesRepository.deleteAllInBatch();
    }

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
        String url_get = "/api/v1/closets/post/" + closets_postId + "/recomm";


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MvcResult result = mockMvc.perform(get(url_get)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andReturn();
    }

    @Test
    @Ignore
    public void ??????_???????????????() throws Exception{
        String token = "Bearer" + AT;
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(get("/api/v1/main/recomm/user")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(10))).andReturn();
    }

    @Test
    public void ??????_?????????() throws Exception{
        String token = "Bearer" + AT;
        String url = "/api/v1/stores/post/" + postId + "/like";
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        // ????????? ??????
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        url = "/api/v1/stores/post/" + Long.toString(Long.parseLong(postId)-1) + "/like";
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(get("/api/v1/main/recomm/best")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();
    }

    @Test
    public void ??????_????????????() throws Exception{
        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        mockMvc.perform(get("/api/v1/main/recomm/latest-product")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();
    }

    @Test
    public void ??????_??????????????????() throws Exception{

        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(get("/api/v1/main/banner").header("Authorization", token))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    public void ?????????_????????????????????????() throws Exception{

        ?????????_????????????????????????();
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
    public void ?????????_????????????????????????() throws Exception{


        testData = new ArrayList<>();
        testData_img_closets = new ArrayList<>();
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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

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
        testData_img_closets.add(img);

    }

    @Test
    public void ????????????_????????????????????????() throws Exception{
        ????????????_????????????????????????();
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
    public void ????????????_????????????????????????() throws Exception{


        testData_post = new ArrayList<>();
        testData_clothes = new ArrayList<>();
        testData_sales = new ArrayList<>();
        testData_img = new ArrayList<>();
        StoresPostClothesRequestDto clothesdto = null;
        StoresPostSalesRequestDto salesdto = null;
        StoresPostRequestDto postinfo = null;
        MockMultipartFile img = null;

        postinfo = StoresPostRequestDto.builder().title("??????").content("???????????????~").price("5000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("?????????")
                .length("??????")
                .color("??????")
                .material("????????????")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("?????????")
                .clothes_color("???????????? ?????????")
                .clothes_size("32")
                .clothes_gender("???")
                .status_score("4")
                .status_tag("??????")
                .status_times("3??? ??????")
                .status_when2buy("1??? ???")
                .user_size("2XL")
                .user_height("~155")
                .user_weight("45~50")
                .transport("??????")
                .clothes_length(36L)
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants2.jpg"));
        testData_img.add(img);


        postinfo = StoresPostRequestDto.builder().title("?????? ????????????").content("?????? 10000?????? ?????????~").price("10000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("????????????")
                .fit("?????????")
                .length("??????")
                .color("??????")
                .material("??????")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("??????")
                .clothes_color("????????? ?????????")
                .clothes_size("M")
                .clothes_gender("???")
                .status_score("2")
                .status_tag("??????")
                .status_times("")
                .status_when2buy("3~6?????? ???")
                .user_size("M")
                .user_height("160~165")
                .user_weight("55~60")
                .transport("?????????")
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top.jpg"));
        testData_img.add(img);

        postinfo = StoresPostRequestDto.builder().title("??????").content("???????????????~").price("3000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("??????")
                .length("???")
                .color("??????")
                .material("?????????")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("?????????")
                .clothes_color("???????????? ????????????")
                .clothes_size("XS")
                .clothes_gender("???")
                .status_score("5")
                .status_tag("??????")
                .status_times("")
                .status_when2buy("~3?????? ???")
                .user_size("S")
                .user_height("180~185")
                .user_weight("75~80")
                .transport("??????")
                .clothes_length(72L)
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/top2.jpg"));
        testData_img.add(img);

        postinfo = StoresPostRequestDto.builder().title("?????????").content("??????????????????~").price("5000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("?????????")
                .large_category("??????")
                .small_category("?????????")
                .fit("?????????")
                .length("?????????")
                .color("??????")
                .material("??????")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("??????")
                .clothes_color("")
                .clothes_size("XL")
                .clothes_gender("???")
                .status_score("4")
                .status_tag("??????")
                .status_times("3??? ??????")
                .status_when2buy("3~6?????? ???")
                .user_size("L")
                .user_height("175~180")
                .user_weight("65~70")
                .transport("?????????")
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/pants3.jpg"));
        testData_img.add(img);

        postinfo = StoresPostRequestDto.builder().title("??????????????????").content("?????????").price("1000").build();
        testData_post.add(postinfo);
        clothesdto = StoresPostClothesRequestDto.builder()
                .style("???????????????")
                .large_category("?????????")
                .small_category("????????????")
                .fit("???????????????")
                .length("??????")
                .color("??????")
                .material("?????????")
                .build();
        testData_clothes.add(clothesdto);
        salesdto = StoresPostSalesRequestDto.builder()
                .clothes_brand("?????????")
                .clothes_color("????????? ?????????")
                .clothes_size("2XL")
                .clothes_gender("???")
                .status_score("5")
                .status_tag("??????")
                .status_times("3??? ??????")
                .status_when2buy("~3?????? ???")
                .user_size("XL")
                .user_height("175~180")
                .user_weight("80~85")
                .transport("??????")
                .build();
        testData_sales.add(salesdto);
        img =  new MockMultipartFile("image", "test.jpg",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/one-piece.jpg"));
        testData_img.add(img);

    }

}
