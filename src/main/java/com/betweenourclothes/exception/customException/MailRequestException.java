package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class MailRequestException extends CustomException {
    public MailRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
