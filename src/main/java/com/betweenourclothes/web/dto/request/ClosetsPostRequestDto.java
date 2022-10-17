package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.members.Members;
import lombok.*;

@Getter
@NoArgsConstructor
public class ClosetsPostRequestDto {
    private String content;

    @Builder
    public ClosetsPostRequestDto(String content){
        this.content = content;
    }

    public Closets toEntity(Members member){
        return Closets.builder().author(member).content(this.content).build();
    }

}
