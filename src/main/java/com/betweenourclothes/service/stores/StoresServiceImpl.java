package com.betweenourclothes.service.stores;

import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.clothes.repository.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.stores.*;
import com.betweenourclothes.domain.stores.repository.*;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.exception.customException.StoresPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import com.betweenourclothes.web.dto.request.StoresPostSalesRequestDto;
import com.betweenourclothes.web.dto.request.StoresSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.StoresPostResponseDto;
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

    private final SalesInfoUserRepository salesInfoUserRepository;
    private final SalesInfoClothesRepository salesInfoClothesRepository;
    private final SalesInfoStatusRepository salesInfoStatusRepository;

    @Transactional
    @Override
    public Long post(StoresPostRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs) {

        // 1. 회원 체크
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        // 2. 옷 정보 & 스타일 형식
        Style style = styleRepository.findByName(clothesinfo.getStyle())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(clothesinfo.getMaterial())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(clothesinfo.getColor())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                clothesinfo.getLarge_category(), clothesinfo.getSmall_category(), clothesinfo.getLength(), clothesinfo.getFit()
        ).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        // 판매 정보 체크
        SalesInfoClothes salesInfoClothes = salesInfoClothesRepository.findByAll(salesinfo.getClothes_brand(),
                salesinfo.getClothes_gender(), salesinfo.getClothes_size(), salesinfo.getClothes_color())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        SalesInfoStatus salesInfoStatus = salesInfoStatusRepository.findByAll(salesinfo.getStatus_tag(),
                        salesinfo.getStatus_score(), salesinfo.getStatus_times(), salesinfo.getStatus_when2buy(),
                        salesinfo.getTransport())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        SalesInfoUser salesInfoUser = salesInfoUserRepository.findByAll(salesinfo.getUser_size(),
                        salesinfo.getUser_weight(), salesinfo.getUser_height())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));



        // 3. 이미지 테이블에 추가
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            clothesImageRepository.save(imgEntity);
            imgArr.add(imgEntity);
        }

        // 4. 게시글 게시
        Stores post = clothesinfo.toEntity(member, style, material, color, clothesInfo, imgArr,
            salesInfoClothes, salesInfoUser, salesInfoStatus, salesinfo.getClothes_length(),
                StoresPostSalesRequestDto.string2enum(salesinfo.getStatus()));

        storesRepository.save(post);
        member.updateStoresPosts(post);
        for(ClothesImage img: imgArr){
            img.updatePostId(post);
        }
        return post.getId();
    }

    @Transactional
    @Override
    public void update(Long id, StoresPostRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        //옷 & 스타일 체크
        Style style = styleRepository.findByName(clothesinfo.getStyle())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(clothesinfo.getMaterial())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(clothesinfo.getColor())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                clothesinfo.getLarge_category(), clothesinfo.getSmall_category(), clothesinfo.getLength(), clothesinfo.getFit()
        ).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        // 판매 정보 체크
        SalesInfoClothes salesInfoClothes = salesInfoClothesRepository.findByAll(salesinfo.getClothes_brand(),
                        salesinfo.getClothes_gender(), salesinfo.getClothes_size(), salesinfo.getClothes_color())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        SalesInfoStatus salesInfoStatus = salesInfoStatusRepository.findByAll(salesinfo.getStatus_tag(),
                        salesinfo.getStatus_score(), salesinfo.getStatus_times(), salesinfo.getStatus_when2buy(),
                        salesinfo.getTransport())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        SalesInfoUser salesInfoUser = salesInfoUserRepository.findByAll(salesinfo.getUser_size(),
                        salesinfo.getUser_weight(), salesinfo.getUser_height())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));


        // 게시글 찾고 업데이트
        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        post.update(style, material, color, clothesInfo, salesInfoClothes, salesInfoStatus, salesInfoUser,
                salesinfo.getClothes_length(), clothesinfo.getTitle(), clothesinfo.getContent(),
                StoresPostSalesRequestDto.string2enum(salesinfo.getStatus()));

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
    public StoresPostResponseDto findPostById(Long id) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : post.getImages()){
            returnArr.add(image.toByte(-1, -1));
        }

        StoresPostResponseDto responseDto = StoresPostResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .clothes_brand(post.getSalesInfo_clothes().getClothes_brand())
                .clothes_gender(post.getSalesInfo_clothes().getClothes_gender())
                .clothes_size(post.getSalesInfo_clothes().getClothes_size())
                .clothes_length(post.getClothes_length())
                .clothes_color(post.getSalesInfo_clothes().getClothes_color())
                .status_tag(post.getSalesInfo_status().getStatus_tag())
                .status_score(post.getSalesInfo_status().getStatus_score())
                .status_times(post.getSalesInfo_status().getStatus_times())
                .status_when2buy(post.getSalesInfo_status().getStatus_when2buy())
                .transport(post.getSalesInfo_status().getTransport())
                .user_size(post.getSalesInfo_user().getUser_size())
                .user_height(post.getSalesInfo_user().getUser_height())
                .user_weight(post.getSalesInfo_user().getUser_weight())
                .sales_status(StoresPostResponseDto.enum2String(post.getStatus()))
                .images(returnArr).id(id).build();
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


        List<String> titles = new ArrayList<>();
        List<String> modifiedDates = new ArrayList<>();
        List<Long> postId = new ArrayList<>();
        List<byte[]> returnArr = new ArrayList<>();


        Stores post = null;
        for(ClothesImage image : images.getContent()){
            if(image.getStores_post_id().getStatus() == SalesStatus.SALES){
                returnArr.add(image.toByte(300, 300));
                postId.add(image.getStores_post_id().getId());
                titles.add(image.getStores_post_id().getTitle());
                modifiedDates.add(image.getStores_post_id().getModifiedDate().toString());
            }
        }

        StoresThumbnailsResponseDto responseDto = StoresThumbnailsResponseDto.builder()
                .title(titles).modified_date(modifiedDates)
                .id(postId).images(returnArr).length(returnArr.size()).build();
        return responseDto;
    }
}
