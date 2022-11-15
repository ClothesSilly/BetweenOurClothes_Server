package com.betweenourclothes.web.dto.response.stores;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import com.betweenourclothes.web.dto.request.stores.StoresPostCommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class StoresPostCommentsResponseDto {
    public String comments;
    public String nickname;
    public String createdTime;
    public byte[] image;

    @Builder
    public StoresPostCommentsResponseDto(String comments, String nickname, String createdTime, byte[] image){
        this.comments = comments;
        this.nickname = nickname;
        this.createdTime = createdTime;
        this.image = image;
    }



}
