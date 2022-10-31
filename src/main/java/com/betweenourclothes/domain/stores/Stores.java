package com.betweenourclothes.domain.stores;

import com.betweenourclothes.domain.BaseTimeEntity;
import com.betweenourclothes.domain.Posts;
import com.betweenourclothes.domain.clothes.*;
import com.betweenourclothes.domain.members.Members;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Stores extends Posts {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;


    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn()
    @JoinColumn(name="stores_post_id")
    private List<ClothesImage> images;

    @Builder
    public Stores(Members author, Style style, List<ClothesImage> imgs, String title, String content,
                   Materials materials, Colors colors, ClothesInfo clothesInfo
    ){
        this.author = author;
        this.style = style;
        this.images = imgs;
        this.materials = materials;
        this.colors = colors;
        this.clothesInfo = clothesInfo;
        this.content = content;
        this.title = title;
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
