package com.betweenourclothes.domain.main;

import com.betweenourclothes.domain.BaseTimeEntity;
import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.stores.Stores;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Recomm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Closets closets;

    @ManyToOne(fetch = FetchType.EAGER)
    private Stores stores;

    @Builder
    public Recomm(Stores store, Closets closet){
        this.closets = closet;
        this.stores = store;
    }

    public void update(Stores store, Closets closet){
        this.closets = closet;
        this.stores = store;
    }

}
