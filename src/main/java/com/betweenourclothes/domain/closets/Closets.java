package com.betweenourclothes.domain.closets;

import com.betweenourclothes.domain.BaseTimeEntity;
import com.betweenourclothes.domain.clothes.Style;
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

    // 스타일
    @JoinColumn(referencedColumnName = "name")
    @ManyToOne(fetch = FetchType.EAGER)
    private Style style;

    //@OneToMany
    //@JoinColumn(name = images)
    //private List<Images> images;

    @Builder
    public Closets(Members author, String content, Style style){
        this.author = author;
        this.content = content;
        this.style = style;
    }
}