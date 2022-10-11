package com.betweenourclothes.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 400 */
    REQUEST_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었음"),
    AUTHCODE_NOT_MATCHED(HttpStatus.BAD_REQUEST, "인증 코드가 잘못되었음"),

    /* 404 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없음"),

    /* 409 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임"),

    /* 500 */
    MAIL_MSG_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 메시지 생성 에러"),
    MAIL_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 에러")
    ;
    private HttpStatus code;
    private final String message;
}
