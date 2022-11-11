package com.betweenourclothes.service.main;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.closets.repository.ClosetsRepository;
import com.betweenourclothes.domain.main.Recomm;
import com.betweenourclothes.domain.main.repository.RecommRepository;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.repository.StoresRepository;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.MainException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.main.MainRecommPostRequestDto;
import com.betweenourclothes.web.dto.response.main.MainBannerResponseDto;
import com.betweenourclothes.web.dto.response.main.MainRecommPostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

    private final RecommRepository recommRepository;
    private final MembersRepository membersRepository;
    private final StoresRepository storesRepository;
    private final ClosetsRepository closetsRepository;

    @Transactional
    @Override
    public void post_recomm(Long id, MainRecommPostRequestDto requestDto) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        Closets closet = closetsRepository.findById(id).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));

        for(Long sid : requestDto.getStores_id()){
            Stores store = storesRepository.findById(sid).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));
            Recomm entity = requestDto.toEntity(store, closet);
            recommRepository.save(entity);
            closet.updateRecomm(entity);
        }
    }

    @Transactional
    @Override
    public void update_recomm(Long id, MainRecommPostRequestDto requestDto) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        Closets closet = closetsRepository.findById(id).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));
        for(Recomm recomm : closet.getRecomms()){
            recommRepository.delete(recomm);
        }

        closet.initRecomm();
        for(Long sid : requestDto.getStores_id()){
            Stores store = storesRepository.findById(sid).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));
            Recomm entity = requestDto.toEntity(store, closet);
            recommRepository.save(entity);
            closet.updateRecomm(entity);
        }
    }

    @Transactional
    @Override
    public List<MainRecommPostResponseDto> get_recomm(Long id) {

        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new MainException(ErrorCode.USER_NOT_FOUND));

        Closets closet = closetsRepository.findById(id).orElseThrow(()->new MainException(ErrorCode.ITEM_NOT_FOUND));

        List<MainRecommPostResponseDto> returnArr = new ArrayList<>();
        for(Recomm entity: closet.getRecomms()){
            MainRecommPostResponseDto dto = MainRecommPostResponseDto.builder().image(entity.getStores().getImages().get(0))
                    .id(entity.getStores().getId()).build();
            returnArr.add(dto);
        }

        return returnArr;
    }

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


}
