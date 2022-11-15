package com.betweenourclothes.web.dto;

import com.betweenourclothes.domain.clothes.ClothesImage;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StoresCommentTmpDto {
    public String comments;
    public String nickname;
    public String createdTime;
    public String image;

    @Builder
    public StoresCommentTmpDto(String comments, String nickname, LocalDateTime createdTime, String image){
        this.comments = comments;
        this.nickname = nickname;
        this.createdTime = createdTime.toString();
        this.image = image;
    }

    public byte[] toByte(String path, int width, int height){
        try {
            BufferedImage bi = null;
            if(width == -1 && height == -1){
                bi = ImageIO.read(new File(path));
            } else{
                bi = ImageIO.read(new File(path));
                bi = Thumbnails.of(bi)
                        .size(width, height)
                        .asBufferedImage();
            }

            // png를 파괴하자
            if(bi.getTransparency() != BufferedImage.OPAQUE){
                int w = bi.getWidth();
                int h = bi.getHeight();
                int[] pixels = new int[w*h];

                bi.getRGB(0,0,w,h,pixels,0,w);
                BufferedImage jpg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
                jpg.setRGB(0,0,w,h,pixels,0,w);
                bi = jpg;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] imageByteArr = baos.toByteArray();
            baos.close();

            return imageByteArr;
        } catch (Exception e){
            throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }
}
