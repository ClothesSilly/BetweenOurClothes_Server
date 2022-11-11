package com.betweenourclothes.web.dto.response.stores;


import com.betweenourclothes.domain.clothes.ClothesImage;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StoresThumbnailsResponseDto {
    public byte[] image;
    public String title;
    public Long id;
    public String modified_date;
    public String price;
    public String content;
    public String transport;

    public StoresThumbnailsResponseDto(ClothesImage image, String title, Long id, LocalDateTime modified_date,
                                       String price, String content, String transport){
        this.image = image.toByte(300, 300);
        this.title = title;
        this.id = id;
        this.modified_date = modified_date.toString();
        this.price = price;
        this.content = content;
        this.transport = transport;
    }
}
