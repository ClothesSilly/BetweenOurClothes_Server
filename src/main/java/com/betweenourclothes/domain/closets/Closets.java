package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.BaseTimeEntity;
import com.betweenourclothes.domain.members.Members;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Closets extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @JoinColumn(name="user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Members author;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 옷 정보: DB에 저장
    //@Enumerated(EnumType.STRING)
    //private ClothesCategoryL large_category;
    //private String small_category;
    //private String option_length;
    //private String option_material;
    //private String option_fit;
    //private String option_color;

    // 스타일
    //private String style;

    //@OneToMany
    //@JoinColumn(name = images)
    //private List<Images> images;

    @Builder
    public Closets(Members author, String content){
        this.author = author;
        this.content = content;
    }
}
