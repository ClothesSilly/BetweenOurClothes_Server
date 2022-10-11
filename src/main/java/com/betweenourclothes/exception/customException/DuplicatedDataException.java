package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class DuplicatedDataException extends CustomException {
    public DuplicatedDataException(ErrorCode errorCode) {
        super(errorCode);
    }
}
