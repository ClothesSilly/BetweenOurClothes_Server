package com.betweenourclothes.domain.clothes;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name="clothes_image")
public class ClothesImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    private String type;

    @Builder
    public ClothesImage(String type){
        this.type = type;
    }

    public void updateImage(MultipartFile img){
        try{
            // 업로드 파일 이름 생성
            // 업로드 파일 식별을 위한 uuid 생성
            String uuid = UUID.randomUUID().toString();
            String path = new File("./src/main/resources/static/images/"+type).getAbsolutePath();
            String uploadedFileName = type+"-" + uuid;

            // 확장자
            String extension = '.' + img.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");

            // 파일 객체 생성: 파일이 저장될 디렉토리를 만듦
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }

            // 파일 객체 생성: 업로드될 파일을 위한 것
            file = new File(path+"/"+uploadedFileName+extension);

            // 전송 후, 파일 경로 반환
            img.transferTo(file);
            this.path = file.getAbsolutePath();
        } catch(NullPointerException e){
            throw new ClosetsPostException(ErrorCode.REQUEST_FORMAT_ERROR);
        } catch (IOException e) {
            throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }

}
