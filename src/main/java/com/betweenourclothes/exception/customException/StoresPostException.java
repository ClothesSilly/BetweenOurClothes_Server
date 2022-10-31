package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class StoresPostException extends CustomException{
    public StoresPostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
