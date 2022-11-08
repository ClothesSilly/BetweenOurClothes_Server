package com.betweenourclothes.web.dto.request.stores;

import com.betweenourclothes.domain.stores.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoresPostSalesRequestDto {
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

    public static SalesStatus string2enum(String status){
        if(status.equals("SOLD")){
            return SalesStatus.SOLD;
        } else{
            return SalesStatus.SALES;
        }
    }
}
