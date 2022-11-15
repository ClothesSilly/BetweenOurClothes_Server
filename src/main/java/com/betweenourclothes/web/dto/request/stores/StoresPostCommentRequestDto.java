package com.betweenourclothes.web.dto.request.stores;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.StoresComments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoresPostCommentRequestDto {
    private String content;

    public StoresComments toEntity(Members user_id, Stores post_id){
        return StoresComments.builder().user_id(user_id).post_id(post_id).content(content).build();
    }
}
