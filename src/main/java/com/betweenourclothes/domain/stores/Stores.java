package com.betweenourclothes.domain.stores;

import com.betweenourclothes.domain.BaseTimeEntity;
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
public class Stores extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

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

    public void updateImage(List<ClothesImage> clothesImages){
        this.images = clothesImages;
    }
}
