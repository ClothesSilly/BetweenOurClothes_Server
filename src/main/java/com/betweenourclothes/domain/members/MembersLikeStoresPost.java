package com.betweenourclothes.domain.members;

import com.betweenourclothes.domain.stores.Stores;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MembersLikeStoresPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    private Members user;

    @ManyToOne(fetch=FetchType.EAGER)
    private Stores store;

    @Builder
    public MembersLikeStoresPost(Members user, Stores store){
        this.user = user;
        this.store = store;
    }
}
