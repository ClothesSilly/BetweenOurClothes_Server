package com.betweenourclothes.domain.closets;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name="closets_image")
public class ClosetsImage {

    @Id
    @Column(length=255)
    private String path;

    private Long post_id;

    @Builder
    public ClosetsImage(String path, Long post_id){
        this.path = path;
        this.post_id = post_id;
    }

}
