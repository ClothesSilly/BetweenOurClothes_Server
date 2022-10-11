package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class RequestFormatException extends CustomException{
    public RequestFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
