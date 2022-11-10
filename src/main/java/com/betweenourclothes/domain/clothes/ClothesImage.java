package com.betweenourclothes.domain.clothes;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class ClothesImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    private Closets closets_post;

    @ManyToOne(fetch = FetchType.EAGER)
    private Stores stores_post;

    @Builder
    public ClothesImage(String type){
        this.type = type;
    }

    public void updateImage(MultipartFile img){
        try{
            // 업로드 파일 이름 생성
            // 업로드 파일 식별을 위한 uuid 생성
            String uuid = UUID.randomUUID().toString();
            String path = new File("src/main/resources/static/images/"+type).getAbsolutePath();
            String uploadedFileName = type+"-" + uuid;

            // 확장자
            String extension = '.' + img.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");

            // 파일 객체 생성: 파일이 저장될 디렉토리를 만듦
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }

            // 파일 객체 생성: 업로드될 파일을 위한 것
            file = new File(path+File.separator+uploadedFileName+extension);

            // 전송 후, 파일 경로 반환
            img.transferTo(file);
            this.path = file.getAbsolutePath();
        } catch(NullPointerException e){
            throw new ClosetsPostException(ErrorCode.REQUEST_FORMAT_ERROR);
        } catch (IOException e) {
            throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }

    public void updatePostId(Closets closets){
        this.closets_post = closets;
    }

    public void updatePostId(Stores stores){
        this.stores_post = stores;
    }

    public byte[] toByte(String path, int width, int height){
        try {
            BufferedImage bi = null;
            if(width == -1 && height == -1){
                bi = ImageIO.read(new File(path));
            } else{
                bi = Thumbnails.of(new File(path))
                        .size(width, height)
                        .asBufferedImage();
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
