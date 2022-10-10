package com.betweenourclothes.exception.exception;

import com.betweenourclothes.exception.ErrorResponseEntity;
import com.betweenourclothes.exception.exception.DuplicatedDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class customExceptionHandler {
    @ExceptionHandler(DuplicatedDataException.class)
    protected ResponseEntity<ErrorResponseEntity> handle(DuplicatedDataException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }
}
