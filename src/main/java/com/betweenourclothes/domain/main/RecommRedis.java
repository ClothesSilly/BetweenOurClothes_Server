package com.betweenourclothes.domain.main;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Getter
@RedisHash(value="recomm", timeToLive = 60*60*24*3)
public class RecommRedis {

    @Id
    @Indexed
    private String closets_post_id;

    private List<Long> stores_post_id;

    @Builder
    public RecommRedis(String closets_post_id, List<Long> stores_post_id){
        this.closets_post_id = closets_post_id;
        this.stores_post_id = stores_post_id;
    }
}
