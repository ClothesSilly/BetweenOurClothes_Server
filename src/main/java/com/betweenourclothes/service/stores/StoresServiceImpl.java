package com.betweenourclothes.service.stores;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.clothes.repository.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresQueryDslRepository;
import com.betweenourclothes.domain.stores.repository.StoresRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.exception.customException.StoresPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import com.betweenourclothes.web.dto.request.StoresSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import com.betweenourclothes.web.dto.response.StoresImagesResponseDto;
import com.betweenourclothes.web.dto.response.StoresThumbnailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StoresServiceImpl implements StoresService{

    private final MembersRepository membersRepository;
    private final StyleRepository styleRepository;
    private final MaterialsRepository materialsRepository;
    private final ColorsRepository colorsRepository;
    private final ClothesInfoRepository clothesInfoRepository;
    private final ClothesImageRepository clothesImageRepository;
    private final StoresRepository storesRepository;
    private final StoresQueryDslRepository storesQueryDslRepository;

    @Transactional
    @Override
    public Long post(StoresPostRequestDto requestDto, List<MultipartFile> imgs) {

        // 1. 회원 체크
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        // 2. 옷 정보 & 스타일 형식 & 판매정보 체크
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(requestDto.getMaterial())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(requestDto.getColor())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                requestDto.getLarge_category(), requestDto.getSmall_category(), requestDto.getLength(), requestDto.getFit()
        ).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));


        // 3. 이미지 테이블에 추가
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            clothesImageRepository.save(imgEntity);
            imgArr.add(imgEntity);
        }

        // 4. 게시글 게시
        Stores post = requestDto.toEntity(member, style, material, color, clothesInfo, imgArr);
        storesRepository.save(post);
        member.updateStoresPosts(post);
        for(ClothesImage img: imgArr){
            img.updatePostId(post);
        }
        return post.getId();
    }

    @Transactional
    @Override
    public void update(Long id, StoresPostRequestDto requestDto, List<MultipartFile> imgs) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        //옷 & 스타일 체크
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(requestDto.getMaterial())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(requestDto.getColor())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                requestDto.getLarge_category(), requestDto.getSmall_category(), requestDto.getLength(), requestDto.getFit()
        ).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));


        System.out.println("....................................................");
        for(Stores all: storesRepository.findAll()){
            System.out.println(all.getId());
        }

        // 게시글 찾고 업데이트
        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        post.update(style, material, color, clothesInfo);

        // 경로에 저장되어 있는 실제 이미지 삭제
        for(ClothesImage img : post.getImages()){
            File file = new File(img.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        // 이미지 테이블의 이미지 삭제
        clothesImageRepository.deleteInBatch(post.getImages());

        // 이미지 테이블에 사진 새로 추가 후 업데이트
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            imgArr.add(imgEntity);
        }
        post.updateImage(imgArr);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        // 회원 체크
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));


        // 게시글 조회
        Stores post = storesRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        // 경로에 저장되어 있는 실제 이미지 삭제
        for(ClothesImage img : post.getImages()){
            File file = new File(img.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        // 게시글 삭제
        // 이미지 테이블의 관련 값도 자동으로 삭제
        storesRepository.deleteById(id);
    }

    @Transactional
    @Override
    public StoresImagesResponseDto findPostById(Long id) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : post.getImages()){
            returnArr.add(image.toByte(-1, -1));
        }

        StoresImagesResponseDto responseDto = StoresImagesResponseDto.builder().images(returnArr).id(id).build();
        return responseDto;
    }

    @Transactional
    @Override
    public StoresThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, StoresSearchCategoryAllRequestDto req) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Page<ClothesImage> images = storesQueryDslRepository.findClothesImagesByAllOptions(pageable, member.getId(),
                req.getNameL(), req.getNameS(),
                req.getFit(), req.getLength(),
                req.getMaterial(), req.getColor());

        List<Long> postId = new ArrayList<>();
        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : images.getContent()){
            returnArr.add(image.toByte(300, 300));
            postId.add(image.getStores_post_id().getId());
        }

        StoresThumbnailsResponseDto responseDto = StoresThumbnailsResponseDto.builder().id(postId).images(returnArr).length(returnArr.size()).build();
        return responseDto;
    }
}
