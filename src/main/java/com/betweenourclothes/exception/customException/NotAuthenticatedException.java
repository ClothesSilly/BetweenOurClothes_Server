package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class NotAuthenticatedException extends CustomException{
    public NotAuthenticatedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
