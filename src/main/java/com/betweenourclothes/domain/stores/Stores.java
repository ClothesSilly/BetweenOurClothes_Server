package com.betweenourclothes.domain.stores;

import com.betweenourclothes.domain.Posts;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersLikeStoresPost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Stores extends Posts {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    private SalesInfoClothes salesInfo_clothes;

    @ManyToOne(fetch = FetchType.EAGER)
    private SalesInfoStatus salesInfo_status;

    @ManyToOne(fetch = FetchType.EAGER)
    private SalesInfoUser salesInfo_user;

    @Enumerated(EnumType.STRING)
    private SalesStatus status;

    private String price;

    private Long clothes_length;

    @OneToMany(mappedBy = "stores_post", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ClothesImage> images;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StoresComments> comments;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MembersLikeStoresPost> likes;

    @Builder
    public Stores(Members author, Style style, List<ClothesImage> imgs, String title, String content,
                   Materials materials, Colors colors, ClothesInfo clothesInfo,
                  SalesInfoClothes salesInfo_clothes, SalesInfoStatus salesInfo_status, SalesInfoUser salesInfo_user,
                  Long clothes_length, SalesStatus status, String price
    ){
        this.author = author;
        this.style = style;
        this.images = imgs;
        this.materials = materials;
        this.colors = colors;
        this.clothesInfo = clothesInfo;
        this.content = content;
        this.title = title;
        this.salesInfo_clothes = salesInfo_clothes;
        this.salesInfo_status = salesInfo_status;
        this.salesInfo_user = salesInfo_user;
        this.clothes_length = clothes_length;
        this.status = status;
        this.price = price;
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    public void update(Style style, Materials materials, Colors colors, ClothesInfo clothesInfo
        ,SalesInfoClothes salesInfo_clothes, SalesInfoStatus salesInfo_status, SalesInfoUser salesInfo_user,
                       Long clothes_length, String title, String content, String price
    ){
        this.style = style;
        this.materials = materials;
        this.colors = colors;
        this.clothesInfo = clothesInfo;
        this.salesInfo_user = salesInfo_user;
        this.salesInfo_status = salesInfo_status;
        this.salesInfo_clothes = salesInfo_clothes;
        this.clothes_length = clothes_length;
        this.title = title;
        this.content = content;
        this.price = price;
    }

    public void updateImage(List<ClothesImage> clothesImages){
        this.images = clothesImages;
    }

    public void updateComments(StoresComments comments){
        this.comments.add(comments);
    }

    public void updateSalesStatus(){
        if(this.status == SalesStatus.SALES){
            this.status = SalesStatus.SOLD;
        } else{
            this.status = SalesStatus.SALES;
        }
    }

    public void updateLikes(MembersLikeStoresPost like){
        this.likes.add(like);
    }

    public void deleteLikes(MembersLikeStoresPost like){
        for(MembersLikeStoresPost l : likes){
            if(l.getStore().getId() == like.getStore().getId() && l.getUser().getId() == like.getUser().getId()){
                likes.remove(like);
                break;
            }
        }
    }

    public void deleteComments(StoresComments comment){
        for(StoresComments c : comments){
            if(c.getId().equals(comment.getId())){
                comments.remove(c);
                break;
            }
        }
    }
}
