package com.betweenourclothes.service.stores;

import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.clothes.repository.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.jwt.stores.Stores;
import com.betweenourclothes.jwt.stores.repository.StoresRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
}
