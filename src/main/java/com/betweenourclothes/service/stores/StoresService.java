package com.betweenourclothes.service.stores;

import com.betweenourclothes.web.dto.request.ClosetsPostRequestDto;
import com.betweenourclothes.web.dto.request.StoresPostRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoresService {
    Long post(StoresPostRequestDto requestDto, List<MultipartFile> imgs);
}
