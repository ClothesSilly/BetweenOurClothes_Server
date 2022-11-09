package com.betweenourclothes.web.dto.response.stores;

import com.betweenourclothes.web.dto.request.stores.StoresPostCommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class StoresPostCommentsResponseDto {
    private String comments;
    private String nickname;
    private String createdTime;

    public StoresPostCommentsResponseDto(String comments, String nickname, LocalDateTime createdTime){
        this.comments = comments;
        this.nickname = nickname;
        this.createdTime = createdTime.toString();
    }

}
