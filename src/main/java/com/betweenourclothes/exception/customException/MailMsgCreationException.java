package com.betweenourclothes.exception.customException;

import com.betweenourclothes.exception.ErrorCode;

public class MailMsgCreationException extends CustomException{
    public MailMsgCreationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
