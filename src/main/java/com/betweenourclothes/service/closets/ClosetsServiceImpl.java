package com.betweenourclothes.service.closets;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.ClosetsRepository;
import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.domain.clothes.ClothesImageRepository;
import com.betweenourclothes.domain.clothes.Style;
import com.betweenourclothes.domain.clothes.StyleRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

@RequiredArgsConstructor
@Service
public class ClosetsServiceImpl implements ClosetsService{

    private final MembersRepository membersRepository;
    private final StyleRepository styleRepository;
    private final ClosetsRepository closetsRepository;

    private final ClothesImageRepository clothesImageRepository;

    @Transactional
    @Override
    public Long post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {

        // 회원 체크
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));

        // 스타일 형식 체크
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        // 이미지 테이블에 추가
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            imgArr.add(imgEntity);
        }

        // 게시
        Closets post = requestDto.toEntity(member, style, imgArr);
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

        //스타일 형식 체크
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        System.out.println("....................................................");
        for(Closets all: closetsRepository.findAll()){
            System.out.println(all.getId());
        }
        // 게시글 찾고 업데이트
        Closets post = closetsRepository.findById(id).orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));
        post.update(style);

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

        System.out.println("....................................................");
        for(Closets all: closetsRepository.findAll()){
            System.out.println(all.getId());
        }

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
}
