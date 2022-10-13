package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class AuthSignUpException extends CustomException{

    public AuthSignUpException(ErrorCode errorCode) {
        super(errorCode);
    }
}
