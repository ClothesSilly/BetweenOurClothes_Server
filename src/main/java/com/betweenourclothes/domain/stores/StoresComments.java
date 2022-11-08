package com.betweenourclothes.domain.stores;

import com.betweenourclothes.domain.Comments;
import com.betweenourclothes.domain.Posts;
import com.betweenourclothes.domain.members.Members;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@NoArgsConstructor
@Entity
public class StoresComments extends Comments {

    @ManyToOne(fetch = FetchType.EAGER)
    protected Stores post;

    @Builder
    public StoresComments(Stores post_id, Members user_id, String content){
        this.post = post_id;
        this.user = user_id;
        this.content = content;
    }
}
