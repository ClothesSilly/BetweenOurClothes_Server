package com.betweenourclothes.service.closets;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.ClosetsRepository;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.response.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.ClosetsThumbnailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

@RequiredArgsConstructor
@Service
public class ClosetsServiceImpl implements ClosetsService{

    private final MembersRepository membersRepository;
    private final StyleRepository styleRepository;
    private final MaterialsRepository materialsRepository;
    private final ColorsRepository colorsRepository;
    private final ClothesInfoRepository clothesInfoRepository;
    private final ClosetsRepository closetsRepository;

    private final ClothesImageRepository clothesImageRepository;

    @Transactional
    @Override
    public Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {

        // 회원 체크
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        // 옷 정보 & 스타일
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(requestDto.getMaterial())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(requestDto.getColor())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                requestDto.getLarge_category(), requestDto.getSmall_category(), requestDto.getLength(), requestDto.getFit()
        ).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));


        // 이미지 테이블에 추가
        List<ClothesImage> imgArr = new ArrayList<>();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>length: " + imgArr.size());
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            clothesImageRepository.save(imgEntity);
            imgArr.add(imgEntity);
        }

        // 게시
        Closets post = requestDto.toEntity(member, style, material, color, clothesInfo, imgArr);
        closetsRepository.save(post);
        member.updateClosetsPosts(post);
        return post.getId();
    }

    @Transactional
    @Override
    public void update(Long id, ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {
        // 회원 체크
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
        for(Closets all: closetsRepository.findAll()){
            System.out.println(all.getId());
        }
        // 게시글 찾고 업데이트
        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));
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

        /*System.out.println("....................................................");
        for(Closets all: closetsRepository.findAll()){
            System.out.println(all.getId());
        }*/

        // 게시글 조회
        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        // 경로에 저장되어 있는 실제 이미지 삭제
        for(ClothesImage img : post.getImages()){
            File file = new File(img.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        // 게시글 삭제
        // 이미지 테이블의 관련 값도 자동으로 삭제
        closetsRepository.deleteById(id);
    }

    @Override
    public ClosetsThumbnailsResponseDto findImagesByCreatedDateDesc(Pageable pageable) {
        // 회원 체크
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Page<ClothesImage> images = closetsRepository.findImagesByMemberIdOrderByCreatedDateDesc(member.getId(),  pageable);
        System.out.println("total page: " + images.getTotalPages());
        System.out.println("total elements: " + images.getTotalElements());
        System.out.println("getNumber: " + images.getNumber());
        System.out.println("getNE: " + images.getNumberOfElements());
        System.out.println("getsize: " + images.getSize());

        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : images.getContent()){
            try {
                InputStream is = new FileInputStream(image.getPath());
                byte[] imageByteArr = IOUtils.toByteArray(is);
                is.close();
                returnArr.add(imageByteArr);
            } catch (Exception e){
                throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
            }
        }

        ClosetsThumbnailsResponseDto responseDto = ClosetsThumbnailsResponseDto.builder().images(returnArr).length(returnArr.size()).build();
        return responseDto;
    }

    @Override
    public ClosetsImagesResponseDto findImagesByPostId(Long id) {

        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : post.getImages()){
            try {
                InputStream is = new FileInputStream(image.getPath());
                byte[] imageByteArr = IOUtils.toByteArray(is);
                is.close();
                returnArr.add(imageByteArr);
            } catch (Exception e){
                throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
            }
        }

        ClosetsImagesResponseDto responseDto = ClosetsImagesResponseDto.builder().images(returnArr).id(id.toString()).build();

        return responseDto;
    }



    /*
    @Override
    public ThumbnailsResponseDto findImagesByCreatedDateDescDisplay() {
        Members member = membersRepository.findByEmail("gunsong2@naver.com")
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        List<ClothesImage> images = closetsRepository.findImagesByIdOrderByCreatedDateDesc(member.getId());


        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : images){
            try {
                InputStream is = new FileInputStream(image.getPath());
                byte[] imageByteArr = IOUtils.toByteArray(is);
                is.close();
                returnArr.add(imageByteArr);
            } catch (Exception e){
                throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
            }
        }

        ThumbnailsResponseDto responseDto = ThumbnailsResponseDto.builder().images(returnArr).build();
        return responseDto;
    }
    */

}

