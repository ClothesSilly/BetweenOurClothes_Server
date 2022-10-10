package com.betweenourclothes.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 400 */
    REQUEST_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었음"),

    /* 404 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없음"),

    /* 409 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임");


    private HttpStatus code;
    private final String message;
}
