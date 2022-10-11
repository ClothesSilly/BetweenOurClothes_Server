package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;


public class UserNotFoundException extends CustomException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
