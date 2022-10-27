package com.betweenourclothes.config.domain.closets;

import com.betweenourclothes.config.domain.clothes.ClothesImage;
import com.betweenourclothes.config.domain.clothes.Style;
import com.betweenourclothes.config.domain.members.Members;
import com.betweenourclothes.config.domain.BaseTimeEntity;
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

    // 옷 정보: DB에 저장

    // 스타일: DB에 저장
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    private Style style;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn()
    @JoinColumn(name="post_id")
    private List<ClothesImage> images;

    @Builder
    public Closets(Members author, Style style, List<ClothesImage> imgs){
        this.author = author;
        this.style = style;
        this.images = imgs;
    }

    public void update(Style style){
        this.style = style;
    }

    public void updateImage(List<ClothesImage> clothesImages){
        this.images = clothesImages;
    }

}
