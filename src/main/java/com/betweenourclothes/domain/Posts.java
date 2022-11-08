package com.betweenourclothes.domain;

import com.betweenourclothes.domain.clothes.ClothesInfo;
import com.betweenourclothes.domain.clothes.Colors;
import com.betweenourclothes.domain.clothes.Materials;
import com.betweenourclothes.domain.clothes.Style;
import com.betweenourclothes.domain.members.Members;
import lombok.Getter;

import javax.persistence.*;

@Getter
@MappedSuperclass
public abstract class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    protected Members author;

    // 옷 정보 & 스타일
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    protected Style style;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    protected Materials materials;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name")
    protected Colors colors;

    @ManyToOne(fetch = FetchType.EAGER)
    protected ClothesInfo clothesInfo;
    
}
