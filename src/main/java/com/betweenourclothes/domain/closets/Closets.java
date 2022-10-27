package com.betweenourclothes.domain.closets;

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
public class Closets extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Members author;

    // 옷 정보 & 스타일
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    private Style style;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    private Materials materials;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    private Colors colors;

    @ManyToOne(fetch = FetchType.EAGER)
    private ClothesInfo clothesInfo;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn()
    @JoinColumn(name="post_id")
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
