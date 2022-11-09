package com.betweenourclothes.web.dto.response.stores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoresPostCommentsResponseDto {
    List<String> comments;
    List<String> nickname;
    List<String> createdTime;
    int length;
}
