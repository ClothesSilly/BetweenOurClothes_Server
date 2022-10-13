package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class AuthTokenException extends CustomException {

    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
