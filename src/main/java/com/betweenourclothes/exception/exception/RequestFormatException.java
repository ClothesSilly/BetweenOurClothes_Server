package com.betweenourclothes.exception.exception;

import com.betweenourclothes.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RequestFormatException extends RuntimeException{
    ErrorCode errorCode;
}
