package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class MainException extends CustomException{
    public MainException(ErrorCode errorCode) {
        super(errorCode);
    }
}
