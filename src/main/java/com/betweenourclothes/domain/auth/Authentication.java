package com.betweenourclothes.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value="authentication")
public class Authentication {

    @Id
    @Indexed
    private String email;
    private String code;
    private String status;

    @Builder
    public Authentication(String email, String code, String status){
        this.email = email;
        this.code = code;
        this.status = status;
    }

    public void updateStatus(String status){
        this.status = status;
    }
}
