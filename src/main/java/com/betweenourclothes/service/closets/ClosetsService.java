package com.betweenourclothes.service.closets;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClosetsService {
    void post(ClosetsPostRequestDto requestDto, List<MultipartFile> imgs);
    //void postImages();
    //update();
    //delete();
    //get();
}
