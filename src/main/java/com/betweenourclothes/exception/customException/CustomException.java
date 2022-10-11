package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CustomException extends RuntimeException{
    ErrorCode errorCode;
}
