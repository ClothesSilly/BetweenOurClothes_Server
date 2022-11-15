package com.betweenourclothes.exception;

import com.betweenourclothes.exception.customException.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class customExceptionHandler {
    @ExceptionHandler({AuthSignUpException.class, AuthSignInException.class, AuthTokenException.class,
            ClosetsPostException.class, MainException.class, StoresPostException.class})
    protected ResponseEntity<ErrorResponseEntity> handle(CustomException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

}
