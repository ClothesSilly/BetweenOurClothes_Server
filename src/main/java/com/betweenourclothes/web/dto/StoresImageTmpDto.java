package com.betweenourclothes.web.dto;

import com.betweenourclothes.domain.clothes.ClothesImage;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StoresImageTmpDto {
    public ClothesImage image;
    public String title;
    public Long id;
    public LocalDateTime modified_date;
    public String price;
    public String content;
    public String transport;

    @Builder
    public StoresImageTmpDto(ClothesImage image, String title, Long id, LocalDateTime modified_date,
                             String price, String content, String transport){
        this.image = image;
        this.title = title;
        this.id = id;
        this.modified_date = modified_date;
        this.price = price;
        this.content = content;
        this.transport = transport;
    }
}
