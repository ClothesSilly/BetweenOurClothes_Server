package com.betweenourclothes.web.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoresThumbnailsResponseDto {
    private List<byte[]> images;
    private List<String> title;
    private List<Long> id;
    private List<String> modified_date;
    private int length;
}
