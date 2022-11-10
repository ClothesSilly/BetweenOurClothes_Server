package com.betweenourclothes.web.dto.response.main;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import lombok.Builder;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Getter
public class MainBannerResponseDto {

    private byte[] image;

    @Builder
    public MainBannerResponseDto(String path){
        try {
            BufferedImage bi = ImageIO.read(new File(path));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpeg", baos);
            byte[] imageByteArr = baos.toByteArray();
            baos.close();
            this.image = imageByteArr;
        } catch (Exception e){
            throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }
}
