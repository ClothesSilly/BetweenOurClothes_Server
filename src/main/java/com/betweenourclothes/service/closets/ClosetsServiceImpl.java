package com.betweenourclothes.service.closets;

import com.betweenourclothes.domain.auth.Authentication;
import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsQueryDslRepository;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.clothes.repository.*;
import com.betweenourclothes.domain.main.RecommRedis;
//import com.betweenourclothes.domain.main.repository.RecommRedisRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.stores.SalesStatus;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignUpException;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.exception.customException.MainException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsRecommPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsThumbnailsResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ClosetsServiceImpl implements ClosetsService{

    private final MembersRepository membersRepository;
    private final StyleRepository styleRepository;
    private final MaterialsRepository materialsRepository;
    private final ColorsRepository colorsRepository;
    private final ClothesInfoRepository clothesInfoRepository;
    private final ClosetsRepository closetsRepository;

    private final ClosetsQueryDslRepository closetsQueryDslRepository;
    private final ClothesImageRepository clothesImageRepository;

    //private final RecommRedisRepository recommRedisRepository;
    private final RedisTemplate redisTemplate;

    private final StoresRepository storesRepository;

    private final RestTemplate restTemplate;


    /*** ?????????
     * 1. post: ??????
     * 2. update: ??????
     * 3. delete: ??????
     * 4. findPostById: ????????? ID??? ????????? ????????????
     * 5. findPostsByAllCategory: ????????? ???????????? ????????????
     * ***/
    @Transactional
    @Override
    public Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {

        // 1. ?????? ??????
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        // 2. ??? ?????? & ????????? ?????? ??????
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(requestDto.getMaterial())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(requestDto.getColor())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                requestDto.getLarge_category(), requestDto.getSmall_category(), requestDto.getLength(), requestDto.getFit()
        ).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));


        // 3. ????????? ???????????? ??????
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            System.out.println(img.getOriginalFilename());
            imgEntity.updateImage(img);
            clothesImageRepository.save(imgEntity);
            imgArr.add(imgEntity);
        }

        // 4. ????????? ??????
        Closets post = requestDto.toEntity(member, style, material, color, clothesInfo, imgArr);
        closetsRepository.save(post);
        member.updateClosetsPosts(post);
        for(ClothesImage img: imgArr){
            img.updatePostId(post);
        }
        return post.getId();
    }

    @Transactional
    @Override
    public void update(Long id, ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {
        // ?????? ??????
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        //??? & ????????? ??????
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(requestDto.getMaterial())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(requestDto.getColor())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                requestDto.getLarge_category(), requestDto.getSmall_category(), requestDto.getLength(), requestDto.getFit()
        ).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        // ????????? ?????? ????????????
        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));
        post.update(style, material, color, clothesInfo);

        // ????????? ???????????? ?????? ?????? ????????? ??????
        for(ClothesImage img : post.getImages()){
            File file = new File(img.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        // ????????? ???????????? ????????? ??????
        clothesImageRepository.deleteInBatch(post.getImages());

        // ????????? ???????????? ?????? ?????? ?????? ??? ????????????
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            imgEntity.updatePostId(post);
            imgArr.add(imgEntity);
        }
        post.updateImage(imgArr);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        // ?????? ??????
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));


        // ????????? ??????
        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        // ????????? ???????????? ?????? ?????? ????????? ??????
        for(ClothesImage img : post.getImages()){
            File file = new File(img.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        // ????????? ??????
        // ????????? ???????????? ?????? ?????? ???????????? ??????
        closetsRepository.deleteById(id);
    }

    @Transactional
    @Override
    public ClosetsImagesResponseDto findPostById(Long id) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : post.getImages()){
            returnArr.add(image.toByte(-1, -1));
        }

        ClosetsImagesResponseDto responseDto = ClosetsImagesResponseDto.builder().images(returnArr).id(id).build();
        return responseDto;
    }

    @Transactional
    @Override
    public Page<ClosetsThumbnailsResponseDto> findPostsByAllCategory(Pageable pageable, ClosetsSearchCategoryAllRequestDto req) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Page<ClosetsThumbnailsResponseDto> responseDto = closetsQueryDslRepository.findPostsByAllOptions(pageable, member.getId(),
                req.getNameL(), req.getNameS(),
                req.getFit(), req.getLength(),
                req.getMaterial(), req.getColor());

        return responseDto;
    }


    /*** ??? ?????? ????????? ??? ??????
     * 1. ??????
     * ***/

    @Transactional
    @Override
    public List<MainRecommPostResponseDto> get_recomm(Long id) {

        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        Closets closet = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        // redis ??????
        //Optional<RecommRedis> optionalRecommRedis =  recommRedisRepository.findById(closet.getId().toString());
        RecommRedis optionalRecommRedis = findRecomm(closet.getId().toString());
        RecommRedis recomm = null;
        if(optionalRecommRedis == null){
            // ?????????, ??????????????? ???????????? ????????? ??? redis??? ??????, ??????
            String url = "http://localhost:45607/api/v1/recomm";
            //json data
            ClosetsRecommPostRequestDto dto = ClosetsRecommPostRequestDto.builder().clothes_info(closet.getClothesInfo().getId())
                    .color(closet.getColors().getName())
                    .style(closet.getStyle().getName())
                    .material(closet.getMaterials().getName())
                    .build();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity<>(dto, httpHeaders);

            ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);
            List<Long> stores_post_id_list = new ArrayList<>();
            for(Object i : response.getBody()){
                stores_post_id_list.add(Long.parseLong(String.valueOf((Integer)i)));
            }

            RecommRedis newRecomm = RecommRedis.builder().closets_post_id(closet.getId().toString()).stores_post_id(stores_post_id_list).build();

            final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(closet.getId().toString(), newRecomm);
            redisTemplate.expire(closet.getId().toString(), 3, TimeUnit.DAYS);
            //recommRedisRepository.save(newRecomm);
            recomm = newRecomm;
        } else{
            recomm = optionalRecommRedis;
        }

        List<MainRecommPostResponseDto> returnArr = new ArrayList<>();
        // ?????? ?????? ????????????
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

            MainRecommPostResponseDto dto = MainRecommPostResponseDto.builder().image(stores.getImages().get(0))
                    .id(pid).build();
            returnArr.add(dto);

            if(returnArr.size()==10){
                break;
            }
        }

        return returnArr;
    }

    @Transactional
    private RecommRedis findRecomm(String id){
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        ObjectMapper objectMapper = new ObjectMapper();
        RecommRedis data = objectMapper.convertValue(valueOperations.get(id), RecommRedis.class);
        return data;
    }
}

