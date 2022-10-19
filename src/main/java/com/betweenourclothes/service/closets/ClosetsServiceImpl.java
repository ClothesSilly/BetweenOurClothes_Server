package com.betweenourclothes.service.closets;

import com.betweenourclothes.domain.closets.ClosetsRepository;
import com.betweenourclothes.domain.clothes.ClothesImage;
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

@RequiredArgsConstructor
@Service
public class ClosetsServiceImpl implements ClosetsService{

    private final MembersRepository membersRepository;
    private final StyleRepository styleRepository;
    private final ClosetsRepository closetsRepository;
    @Transactional
    @Override
    public void post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs) {

        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.USER_NOT_FOUND));
        Style style = styleRepository.findByName(requestDto.getStyle())
                .orElseThrow(()->new ClosetsPostException(ErrorCode.ITEM_NOT_FOUND));

        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("closets").build();
            imgEntity.updateImage(img);
            imgArr.add(imgEntity);
        }

        closetsRepository.save(requestDto.toEntity(member, style, imgArr));
    }
}
