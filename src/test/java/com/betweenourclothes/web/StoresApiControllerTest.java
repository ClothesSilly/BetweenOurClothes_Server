package com.betweenourclothes.web;

import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.repository.ClothesImageRepository;
import com.betweenourclothes.domain.members.MembersLikeStoresPost;
import com.betweenourclothes.domain.members.repository.MembersLikeStoresPostRepository;
import com.betweenourclothes.domain.stores.SalesStatus;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresCommentsRepository;
import com.betweenourclothes.domain.stores.repository.StoresRepository;
import com.betweenourclothes.jwt.JwtTokenProvider;
import com.betweenourclothes.web.dto.request.auth.AuthSignInRequestDto;
import com.betweenourclothes.web.dto.request.stores.*;
import com.betweenourclothes.web.dto.response.auth.AuthSignInResponseDto;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private StoresCommentsRepository storesCommentsRepository;

    @Autowired
    private MembersLikeStoresPostRepository membersLikeStoresPostRepository;

    private String AT;
    private String postId;

    private List<StoresPostRequestDto> testData_post;
    private List<StoresPostClothesRequestDto> testData_clothes;
    private List<StoresPostSalesRequestDto> testData_sales;
    private List<MockMultipartFile> testData_img;

    @Before
    public void ?????????_????????????????????????() throws Exception {
        ?????????();
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
    public void ????????????_???????????????_?????????_?????????() throws Exception{
        String token = "Bearer" + AT;
        String url = "/api/v1/stores/post/" + postId + "/like";
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        // ????????? ??????
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        // ?????? ??????
        url = "/api/v1/stores/post/" + postId + "/comment";
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        for(int i=0; i<3; i++){
            StoresPostCommentRequestDto dto = StoresPostCommentRequestDto.builder().content("?????? " + Integer.toString(i)).build();
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(dto);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                    .andExpect(status().isOk()).andReturn();
        }

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(3))).andReturn();

        String url_get = "http://localhost:" + port + "/api/v1/stores/post/" + postId;
        HttpEntity<Long> req = new HttpEntity<>(header);

        ResponseEntity<StoresPostResponseDto> resp
                = restTemplate.exchange(url_get, HttpMethod.GET, req, StoresPostResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(Long.parseLong(postId));
        assertThat(resp.getBody().getLike()).isEqualTo(true);
        assertThat(resp.getBody().getLikes_count()).isEqualTo(1);
        assertThat(resp.getBody().getComments_count()).isEqualTo(3);
        assertThat(resp.getBody().getTitle()).isEqualTo("??????????????????");
        assertThat(resp.getBody().getLike()).isEqualTo(true);
        assertThat(resp.getBody().getSales_status()).isEqualTo("SALES");
    }

    @Test
    public void ????????????_??????() throws Exception{
        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        String data = "??????";
        StoresPostSearchRequestDto req = StoresPostSearchRequestDto.builder().keyword(data).build();
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/stores/post/search?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

    }

    @Test
    public void ????????????_????????????????????????() throws Exception{
        String token = "Bearer" + AT;
        String url = "http://localhost:" + port + "/api/v1/stores/post/" + postId + "/status";

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);

        HttpEntity<Long> req = new HttpEntity<>(header);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, req, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Stores> all = storesRepository.findAll();
        Stores last = all.get(all.size()-1);
        assertThat(last.getStatus()).isEqualTo(SalesStatus.SOLD);

    }

    @Test
    public void ????????????_???_????????????????????????() throws Exception{
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

        // ????????? ????????????
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/stores/post/like?page=0")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2))).andReturn();


        // ????????? ??????
        url = "/api/v1/stores/post/" + Long.toString(Long.parseLong(postId)-1) + "/like";
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        url = "/api/v1/stores/post/" + Long.toString(Long.parseLong(postId)) + "/like";
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        List<MembersLikeStoresPost> likes = membersLikeStoresPostRepository.findAll();
        assertThat(likes.size()).isEqualTo(0);
    }

    @Test
    public void ????????????_????????????_??????_??????_??????() throws Exception{
        String token = "Bearer" + AT;
        String url =  "/api/v1/stores/post/" + postId + "/comment";

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        for(int i=0; i<3; i++){
            StoresPostCommentRequestDto dto = StoresPostCommentRequestDto.builder().content("?????? " + Integer.toString(i)).build();
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(dto);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                    .andExpect(status().isOk()).andReturn();
        }

        MvcResult result = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(3))).andReturn();

        //??????
        String url_delete =  "/api/v1/stores/post/" + postId + "/comment/1";
        mockMvc.perform(delete(url_delete).header("Authorization", token))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(2))).andReturn();
    }

    @Test
    public void ????????????_?????????????????????() throws Exception{
        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        String data1 = "??????";
        String data2 = "????????????";
        String data3 = "?????????";
        String color = "??????";
        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder().color(color).nameL("??????").nameS("????????????").fit(data3).build();

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        MvcResult result = mockMvc.perform(post("/api/v1/stores/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(1)))
                .andReturn();
    }

    @Test
    public void ????????????_????????????????????????() throws Exception{
        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        String data1 = "??????";
        String data2 = "????????????";
        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder()
                .nameL(data1).nameS(data2).build();

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(req);

        MvcResult result = mockMvc.perform(post("/api/v1/stores/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON).content(content).header("Authorization", token))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(1)))
                .andReturn();


        data1 = "??????";
        data2 = "?????????";

        req = StoresSearchCategoryAllRequestDto.builder().nameL(data1).nameS(data2).build();

        mapper = new ObjectMapper();
        content = mapper.writeValueAsString(req);

        result = mockMvc.perform(post("/api/v1/stores/post/category?page=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content).header("Authorization", token))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(2)))
                .andReturn();

    }

    @Test
    public void ????????????_?????????????????????() throws Exception{
        String token = "Bearer" + AT;

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder().nameL("??????").build();
        String data2json = new ObjectMapper().writeValueAsString(req);
        System.out.println(data2json);
        MvcResult result = mockMvc.perform(post("/api/v1/stores/post/category?page=0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data2json)
                .header("Authorization", token)).andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(2)))
                .andReturn();

    }

    @Test
    public void ????????????_?????????????????????() throws Exception{

        String token = "Bearer" + AT;

        StoresSearchCategoryAllRequestDto req = StoresSearchCategoryAllRequestDto.builder().build();
        String data2json = new ObjectMapper().writeValueAsString(req);
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        MvcResult result = mockMvc.perform(post("/api/v1/stores/post/category?page=0").header("Authorization", token)
                        .content(data2json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content", hasSize(5)))
                .andReturn();
    }

    @Test
    public void ????????????_?????????id???????????????() throws Exception{
        String token = "Bearer" + AT;
        String url_get = "http://localhost:" + port + "/api/v1/stores/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(header);

        ResponseEntity<StoresPostResponseDto> resp
                = restTemplate.exchange(url_get, HttpMethod.GET, req, StoresPostResponseDto.class);

        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo(Long.parseLong(postId));
        assertThat(resp.getBody().getTitle()).isEqualTo("??????????????????");
        assertThat(resp.getBody().getLike()).isEqualTo(false);
        assertThat(resp.getBody().getSales_status()).isEqualTo("SALES");
    }

    @Test
    public void ????????????_???????????????() throws Exception{
        String token = "Bearer" + AT;
        String url_delete = "http://localhost:" + port + "/api/v1/stores/post/" + postId;

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        HttpEntity<Long> req = new HttpEntity<>(header);

        ResponseEntity<String> resp = restTemplate.exchange(url_delete, HttpMethod.DELETE, req, String.class);
        System.out.println(resp.getBody());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        Optional<Stores> post = storesRepository.findById(Long.parseLong(postId));
        assertThat(post.isPresent()).isEqualTo(false);
    }

    @Test
    public void ????????????_???????????????() throws Exception{
        String token = "Bearer" + AT;
        String url_patch = "http://localhost:" + port + "/api/v1/stores/post/" + postId;

        // ?????????
        StoresPostRequestDto reqDto3 = StoresPostRequestDto.builder().title("????????? ?????????~!~!").content("50000??????")
                .price("50000").build();
        StoresPostClothesRequestDto reqDto = StoresPostClothesRequestDto.builder().style("?????????")
                .large_category("?????????").small_category("????????????").length("??????").fit("??????")
                .color("??????").material("??????").build();
        StoresPostSalesRequestDto reqDto2 = StoresPostSalesRequestDto.builder()
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
        ObjectMapper mapper = new ObjectMapper();
        String dto2Json3 = mapper.writeValueAsString(reqDto3);
        String dto2Json = mapper.writeValueAsString(reqDto);
        String dto22Json = mapper.writeValueAsString(reqDto2);
        MockPart id = new MockPart("id", postId.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile dtofile = new MockMultipartFile("clothes_data", "", "application/json", dto2Json.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile dtofile2 = new MockMultipartFile("sales_data", "", "application/json", dto22Json.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile dtofile3 = new MockMultipartFile("post_data", "", "application/json", dto2Json3.getBytes(StandardCharsets.UTF_8));

        // ??????
        MockMultipartFile file1 = new MockMultipartFile("image", "test4.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));
        MockMultipartFile file2 = new MockMultipartFile("image", "test4.png",
                "multipart/form-data", new FileInputStream("src/test/resources/static/images/test.png"));


        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        mockMvc.perform(multipart(url_patch).part(id).file(dtofile).file(dtofile2).file(dtofile3).file(file1).file(file2).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .with(r -> { r.setMethod("PUT"); return r; }))
                .andDo(print()).andExpect(status().isOk());


        List<Stores> stores = storesRepository.findAll();
        Stores last = stores.get(stores.size()-1);
        assertThat(last.getId()).isEqualTo(Long.parseLong(postId));
        assertThat(last.getStyle().getName()).isEqualTo("?????????");
        assertThat(last.getClothesInfo().getCategoryL()).isEqualTo("?????????");
        assertThat(last.getClothesInfo().getCategoryS()).isEqualTo("????????????");
        assertThat(last.getClothesInfo().getLength()).isEqualTo("??????");
        assertThat(last.getClothesInfo().getFit()).isEqualTo("??????");
        assertThat(last.getColors().getName()).isEqualTo("??????");
        assertThat(last.getMaterials().getName()).isEqualTo("??????");
        assertThat(last.getTitle()).isEqualTo("????????? ?????????~!~!");
        assertThat(last.getSalesInfo_clothes().getClothes_brand()).isEqualTo("??????");
        assertThat(last.getSalesInfo_user().getUser_size()).isEqualTo("M");
        assertThat(last.getSalesInfo_status().getStatus_score()).isEqualTo("2");

    }

    @Test
    public void ????????????_????????????????????????() throws Exception{
        ????????????_????????????????????????();
        String token = "Bearer" + AT;

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
        List<ClothesImage> images = clothesImageRepository.findAll();
        assertThat(images.size()).isNotEqualTo(0);
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
