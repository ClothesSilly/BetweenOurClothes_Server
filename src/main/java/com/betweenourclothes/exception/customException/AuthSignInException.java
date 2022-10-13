package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class AuthSignInException extends CustomException{

    public AuthSignInException(ErrorCode errorCode) {
        super(errorCode);
    }
}
