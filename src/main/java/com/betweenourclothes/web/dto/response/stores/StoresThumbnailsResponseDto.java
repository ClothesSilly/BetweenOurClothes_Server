package com.betweenourclothes.web.dto.response.stores;


import com.betweenourclothes.domain.clothes.ClothesImage;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StoresThumbnailsResponseDto {
    private byte[] image;
    private String title;
    private Long id;
    private String modified_date;
    private String price;
    private String content;
    private String transport;

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
