package com.betweenourclothes.web.dto.response.stores;

import com.betweenourclothes.domain.stores.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoresPostResponseDto {

    private List<byte[]> images;
    private byte[] profile_images;
    private String title;
    private String content;
    private String price;
    private String clothes_brand;
    private String clothes_gender;
    private String clothes_size;
    private String clothes_color;
    private String status_tag;
    private String status_score;
    private String status_times;
    private String status_when2buy;
    private String transport;
    private String user_size;
    private String user_weight;
    private String user_height;
    private Long clothes_length;
    private Long id;
    private String sales_status;
    private String nickname;
    private String createdTime;
    private Boolean like;


    public static String enum2String(SalesStatus status){
        if(status == SalesStatus.SALES){
            return "SALES";
        } else{
            return "SOLD";
        }
    }
}
