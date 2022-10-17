package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class ClosetsPostException extends CustomException{
    public ClosetsPostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
