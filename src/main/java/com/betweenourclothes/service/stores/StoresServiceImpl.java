package com.betweenourclothes.service.stores;

import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.clothes.repository.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersLikeStoresPost;
import com.betweenourclothes.domain.members.repository.MembersLikeStoresPostRepository;
import com.betweenourclothes.domain.members.repository.MembersQueryDslRepository;
import com.betweenourclothes.domain.members.repository.MembersRepository;
import com.betweenourclothes.domain.stores.*;
import com.betweenourclothes.domain.stores.SalesInfoClothes;
import com.betweenourclothes.domain.stores.SalesInfoStatus;
import com.betweenourclothes.domain.stores.SalesInfoUser;
import com.betweenourclothes.domain.stores.repository.*;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.StoresPostException;
import com.betweenourclothes.jwt.SecurityUtil;
import com.betweenourclothes.web.dto.request.stores.*;
import com.betweenourclothes.web.dto.response.stores.StoresPostCommentsResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresPostResponseDto;
import com.betweenourclothes.web.dto.response.stores.StoresThumbnailsResponseDto;
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
    private final StoresCommentsRepository storesCommentsRepository;

    private final MembersLikeStoresPostRepository membersLikeStoresPostRepository;

    private final MembersQueryDslRepository membersQueryDslRepository;


    /*** ?????????
     * 1. post: ??????
     * 2. update: ??????
     * 3. delete: ??????
     * 4. findPostById: ????????? ID??? ????????? ????????????
     * 5. findImagesByAllCategory: ????????? ???????????? ????????????
     * ***/

    @Transactional
    @Override
    public Long post(StoresPostRequestDto postinfo, StoresPostClothesRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs) {

        // 1. ?????? ??????
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        // 2. ??? ?????? & ????????? ??????
        Style style = styleRepository.findByName(clothesinfo.getStyle())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(clothesinfo.getMaterial())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(clothesinfo.getColor())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                clothesinfo.getLarge_category(), clothesinfo.getSmall_category(), clothesinfo.getLength(), clothesinfo.getFit()
        ).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        // ?????? ?????? ??????
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


        // 3. ????????? ???????????? ??????
        List<ClothesImage> imgArr = new ArrayList<>();
        for(MultipartFile img: imgs){
            ClothesImage imgEntity = ClothesImage.builder().type("stores").build();
            imgEntity.updateImage(img);
            clothesImageRepository.save(imgEntity);
            imgArr.add(imgEntity);
        }

        // 4. ????????? ??????
        Stores post = Stores.builder().author(member).style(style).imgs(imgArr)
                .title(postinfo.getTitle()).content(postinfo.getContent()).materials(material).
                colors(color).clothesInfo(clothesInfo).salesInfo_clothes(salesInfoClothes).salesInfo_status(salesInfoStatus)
                .salesInfo_user(salesInfoUser).clothes_length(salesinfo.getClothes_length())
                .status(StoresPostSalesRequestDto.string2enum("SALE")).price(postinfo.getPrice()).build();

        storesRepository.save(post);
        member.updateStoresPosts(post);
        for(ClothesImage img: imgArr){
            img.updatePostId(post);
        }
        return post.getId();
    }

    @Transactional
    @Override
    public void update(Long id, StoresPostRequestDto postinfo, StoresPostClothesRequestDto clothesinfo, StoresPostSalesRequestDto salesinfo, List<MultipartFile> imgs) {
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        //??? & ????????? ??????
        Style style = styleRepository.findByName(clothesinfo.getStyle())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Materials material = materialsRepository.findByName(clothesinfo.getMaterial())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Colors color = colorsRepository.findByName(clothesinfo.getColor())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        ClothesInfo clothesInfo = clothesInfoRepository.findByCategoryLAndCategorySAndLengthAndFit(
                clothesinfo.getLarge_category(), clothesinfo.getSmall_category(), clothesinfo.getLength(), clothesinfo.getFit()
        ).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        // ?????? ?????? ??????
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


        // ????????? ?????? ????????????
        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        post.update(style, material, color, clothesInfo, salesInfoClothes, salesInfoStatus, salesInfoUser,
                salesinfo.getClothes_length(), postinfo.getTitle(), postinfo.getContent(), postinfo.getPrice());

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
            ClothesImage imgEntity = ClothesImage.builder().type("stores").build();
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
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));
        
        // ????????? ??????
        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        // ????????? ???????????? ?????? ?????? ????????? ??????
        for(ClothesImage img : post.getImages()){
            File file = new File(img.getPath());
            if(file.exists()){
                file.delete();
            }
        }

        // ????????? ??????
        // ????????? ???????????? ?????? ?????? ???????????? ??????
        storesRepository.deleteById(id);
    }

    @Transactional
    @Override
    public StoresPostResponseDto findPostById(Long id) {
        Members members = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        List<byte[]> returnArr = new ArrayList<>();

        for(ClothesImage image : post.getImages()){
            returnArr.add(image.toByte(-1, -1));
        }

        Boolean status = false;
        for(MembersLikeStoresPost mks : members.getStoresLikes()) {
            if (post.getId() == mks.getStore().getId()){
                status = true;
                break;
            }
        }

        int likes_count = post.getLikes().size();
        int comments_count = post.getComments().size();

        StoresPostResponseDto responseDto = StoresPostResponseDto.builder()
                .like(status)
                .likes_count(likes_count)
                .comments_count(comments_count)
                .profile_images(post.getAuthor().toByte(200, 200))
                .nickname(post.getAuthor().getNickname())
                .createdTime(post.getCreatedDate().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .price(post.getPrice())
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
    public Page<StoresThumbnailsResponseDto> findPostsByAllCategory(Pageable pageable, StoresSearchCategoryAllRequestDto req) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Page<StoresThumbnailsResponseDto> responseDto = storesQueryDslRepository.findPostsByAllOptions(pageable, member.getId(),
                req.getNameL(), req.getNameS(),
                req.getFit(), req.getLength(),
                req.getMaterial(), req.getColor());

        return responseDto;
    }


    /*** ??????
     * 1. comment: ??????
     * 2. findStoresCommentsByPostId: ????????? ID??? ?????? ????????????
     * 3. delete_comments: ?????? ??????
     * ***/

    @Transactional
    @Override
    public void comment(Long id, StoresPostCommentRequestDto requestDto) {

        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        StoresComments comments = requestDto.toEntity(member, post);

        storesCommentsRepository.save(comments);
        member.updateStoresComments(comments);
        post.updateComments(comments);
    }

    @Transactional
    @Override
    public Page<StoresPostCommentsResponseDto> findStoresCommentsByPostId(Pageable pageable, Long id) {

        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        //Page<StoresPostCommentsResponseDto> responseDto = storesQueryDslRepository.findCommentsByUserAndPost(pageable, member.getId(), post.getId());
        Page<StoresPostCommentsResponseDto> responseDto = storesQueryDslRepository.findCommentsByUserAndPost(pageable, post.getId());

        return responseDto;
    }

    @Transactional
    @Override
    public void delete_comments(Long pid, int cno) {

        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));

        Stores post = storesRepository.findById(pid).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        StoresComments comment = post.getComments().get(cno-1);

        member.deleteStoresComments(comment);
        post.deleteComments(comment);
        storesCommentsRepository.delete(comment);
    }


    /*** ???
     * 1. ??? ?????? / ??????
     * 2. ??? ????????????
     * ***/

    @Transactional
    @Override
    public void likes(Long id) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        MembersLikeStoresPost like = membersLikeStoresPostRepository.findByUserAndStore(member, post);
        if(like == null){
            MembersLikeStoresPost entity = MembersLikeStoresPost.builder().user(member).store(post).build();
            membersLikeStoresPostRepository.save(entity);
            member.updateStoresLikes(entity);
            post.updateLikes(entity);

        } else{
            member.deleteStoresLikes(like);
            post.deleteLikes(like);
            membersLikeStoresPostRepository.delete(like);
        }
    }

    @Transactional
    @Override
    public Page<StoresThumbnailsResponseDto> findStoresLikesByMember(Pageable pageable) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Page<StoresThumbnailsResponseDto> responseDto = membersQueryDslRepository.findByUser(pageable, member.getId());

        return responseDto;
    }


    /*** ?????? ??????
     * 1. ?????? ?????? ????????????
     * ***/
    @Transactional
    @Override
    public void updateSalesStatus(Long id){
        membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Stores post = storesRepository.findById(id).orElseThrow(()->new StoresPostException(ErrorCode.ITEM_NOT_FOUND));
        post.updateSalesStatus();
    }


    /*** ????????? ??????
     * 1. ??????
     * ***/
    @Transactional
    @Override
    public Page<StoresThumbnailsResponseDto> findByKeyword(Pageable pageable, StoresPostSearchRequestDto requestDto) {
        Members member = membersRepository.findByEmail(SecurityUtil.getMemberEmail())
                .orElseThrow(()->new StoresPostException(ErrorCode.USER_NOT_FOUND));

        Page<StoresThumbnailsResponseDto> responseDto = storesQueryDslRepository.findByKeyword(pageable, requestDto.getKeyword());

        return responseDto;
    }


}
