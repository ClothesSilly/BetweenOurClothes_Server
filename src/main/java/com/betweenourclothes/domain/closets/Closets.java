package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.Posts;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Closets extends Posts {

    @OneToMany(mappedBy = "closets_post", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn()
    private List<ClothesImage> images;

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

}
