package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class AuthCodeNotMatchedException extends CustomException{
    public AuthCodeNotMatchedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
