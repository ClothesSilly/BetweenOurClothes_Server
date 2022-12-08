package com.betweenourclothes.service.main;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.main.RecommRedis;
import com.betweenourclothes.domain.main.repository.RecommRedisRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.stores.SalesStatus;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresQueryDslRepository;
import com.betweenourclothes.domain.stores.repository.StoresRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.exception.customException.MainException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.ClosetsImageTmpDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsRecommPostRequestDto;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

    private final MembersRepository membersRepository;
    private final StoresRepository storesRepository;

    private final StoresQueryDslRepository storesQueryDslRepository;
    //private final RecommRedisRepository recommRedisRepository;
    private final RestTemplate restTemplate;

    private final RedisTemplate redisTemplate;

    @Transactional
    @Override
    public List<MainBannerResponseDto> get_banner() {

        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        List<MainBannerResponseDto> responseDto = new ArrayList<>();

        String path = System.getProperty("user.home") + "/betweenourclothes/images/banner/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        for(int i=1; i<=3; i++){
            MainBannerResponseDto dto = MainBannerResponseDto.builder().path(path+"banner"+Integer.toString(i)+".jpg").build();
            responseDto.add(dto);
        }

        return responseDto;
    }

    @Transactional
    @Override
    public List<MainRecommResponseDto> get_latest_products() {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        List<MainRecommResponseDto> responseDto = new ArrayList<>();
        List<ClosetsImageTmpDto> resp = storesQueryDslRepository.findLatestProducts();

        for(ClosetsImageTmpDto image : resp){
            Stores post = storesRepository.findById(image.getId()).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));

            responseDto.add(MainRecommResponseDto.builder().image(image.getImage().toByte(300, 300)).id(post.getId())
                            .title(post.getTitle())
                    .comments_cnt(post.getComments().size()).likes_cnt(post.getLikes().size()).build());
        }

        return responseDto;
    }

    @Transactional
    @Override
    public List<MainRecommResponseDto> get_best_products() {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        List<MainRecommResponseDto> responseDto = new ArrayList<>();
        List<ClosetsImageTmpDto> resp = storesQueryDslRepository.findBestProducts();

        for(ClosetsImageTmpDto image : resp){
            Stores post = storesRepository.findById(image.getId()).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));

            responseDto.add(MainRecommResponseDto.builder().image(image.getImage().toByte(300, 300)).id(post.getId())
                            .title(post.getTitle())
                    .comments_cnt(post.getComments().size()).likes_cnt(post.getLikes().size()).build());
        }

        return responseDto;
    }

    @Transactional
    @Override
    public List<MainRecommResponseDto> get_user_recomm_products() {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        List<MainRecommResponseDto> responseDto = new ArrayList<>();
        // redis 확인, 없으면 외부서버에 연결

        if(member.getClosetsPosts().size()==0){
            return responseDto;
        }

        Closets post = member.getClosetsPosts().get(member.getClosetsPosts().size()-1);
        //Optional<RecommRedis> optionalRecommRedis = recommRedisRepository.findById(post.getId().toString());
        RecommRedis optionalRecommRedis = findRecomm(post.getId().toString());
        RecommRedis recomm = null;

        if(optionalRecommRedis == null){
            // 없으면, 추천서버에 연결해서 받아온 후 redis에 저장, 반환
            String url = "http://localhost:45607/api/v1/recomm";
            //json data
            ClosetsRecommPostRequestDto dto = ClosetsRecommPostRequestDto.builder().clothes_info(post.getClothesInfo().getId())
                    .color(post.getColors().getName())
                    .style(post.getStyle().getName())
                    .material(post.getMaterials().getName())
                    .build();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity<>(dto, httpHeaders);

            ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
            List<Long> stores_post_id_list = new ArrayList<>();
            for(Object i : response.getBody()){
                stores_post_id_list.add(Long.parseLong(String.valueOf((Integer)i)));
            }

            RecommRedis newRecomm = RecommRedis.builder().closets_post_id(post.getId().toString()).stores_post_id(stores_post_id_list).build();
            final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(post.getId().toString(), newRecomm);
            redisTemplate.expire(post.getId().toString(), 3, TimeUnit.DAYS);
            recomm = newRecomm;
        } else{
            recomm = optionalRecommRedis;
        }

        for(Long pid: recomm.getStores_post_id()){
            if(pid == -1){
                break;
            }
            Optional<Stores> opt = storesRepository.findById(pid);
            if(!opt.isPresent()){
                continue;
            }
            Stores stores = opt.get();
            if(stores.getStatus().equals(SalesStatus.SOLD)){
                continue;
            }
            MainRecommResponseDto dto = MainRecommResponseDto.builder().image(stores.getImages().get(0).toByte(300, 300))
                    .id(pid).comments_cnt(stores.getComments().size())
                    .title(stores.getTitle())
                    .likes_cnt(stores.getLikes().size()).build();
            responseDto.add(dto);

            if(responseDto.size()==10){
                break;
            }
        }


        return responseDto;
    }

    @Transactional
    private RecommRedis findRecomm(String id){
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        ObjectMapper objectMapper = new ObjectMapper();
        RecommRedis data = objectMapper.convertValue(valueOperations.get(id), RecommRedis.class);
        return data;
    }

}
