package com.betweenourclothes.service.closets;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsQueryDslRepository;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.clothes.repository.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.closets.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.closets.ClosetsSearchCategoryAllRequestDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsImagesResponseDto;
import com.betweenourclothes.web.dto.response.closets.ClosetsThumbnailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

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

    private final ClosetsQueryDslRepository closetsQueryDslRepository;
    private final ClothesImageRepository clothesImageRepository;

    @Transactional
    @Override
    public Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {

        // 1. 회원 체크
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        // 2. 옷 정보 & 스타일 형식 체크
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

    @Transactional
    @Override
    // 게시글 이미지 모두 가져오기
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
    // 전체 카테고리 정보를 이용해 이미지 가져오기
    public ClosetsThumbnailsResponseDto findImagesByAllCategory(Pageable pageable, ClosetsSearchCategoryAllRequestDto req) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        Page<ClothesImage> images = closetsQueryDslRepository.findClothesImagesByAllOptions(pageable, member.getId(),
                req.getNameL(), req.getNameS(),
                req.getFit(), req.getLength(),
                req.getMaterial(), req.getColor());

        List<Long> postId = new ArrayList<>();
        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : images.getContent()){
            returnArr.add(image.toByte(300, 300));
            postId.add(image.getClosets_post().getId());
        }

        ClosetsThumbnailsResponseDto responseDto = ClosetsThumbnailsResponseDto.builder().id(postId).images(returnArr).length(returnArr.size()).build();
        return responseDto;
    }



/*

    @Override
    public ClosetsThumbnailsResponseDto findImagesByCreatedDateDescDisplay() {
        Members member = membersRepository.findByEmail("gunsong2@naver.com")
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        List<ClothesImage> images = closetsRepository.findImagesByMemberIdOrderByCreatedDateDesc(member.getId());


        List<byte[]> returnArr = new ArrayList<>();
        for(ClothesImage image : images){
            try {
                BufferedImage bi = Thumbnails.of(new File(image.getPath()))
                        .size(300, 300)
                        .asBufferedImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "jpeg", baos);
                byte[] imageByteArr = baos.toByteArray();
                baos.close();
                returnArr.add(imageByteArr);
            } catch (Exception e){
                throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
            }
        }

        ClosetsThumbnailsResponseDto responseDto = ClosetsThumbnailsResponseDto.builder().images(returnArr).build();
        return responseDto;
    }
*/

}

