package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.Posts;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.main.Recomm;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Closets extends Posts {
    @OneToMany(mappedBy = "closets_post", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ClothesImage> images;

    @OneToMany(mappedBy= "closets", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Recomm> recomms;

    @Builder
    public Closets(Members author, Style style, List<ClothesImage> imgs,
            Materials materials, Colors colors, ClothesInfo clothesInfo
    ){
        this.author = author;
        this.style = style;
        this.images = imgs;
        this.materials = materials;
        this.colors = colors;
        this.clothesInfo = clothesInfo;
        this.images = new ArrayList<>();
        this.recomms = new ArrayList<>();
    }

    public void update(Style style, Materials materials, Colors colors, ClothesInfo clothesInfo){

        this.style = style;
        this.materials = materials;
        this.colors = colors;
        this.clothesInfo = clothesInfo;
    }

    public void updateImage(List<ClothesImage> clothesImages){
        this.images = clothesImages;
    }

    public void initRecomm(){
        this.recomms = new ArrayList<>();
    }
    public void updateRecomm(Recomm recomm){
        this.recomms.add(recomm);
    }
}
