package com.betweenourclothes.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 400 */
    REQUEST_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었음"),
    NOT_AUTHENTICATED(HttpStatus.BAD_REQUEST, "인증되지 않았음"),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호"),


    /* 401 */
    REFRESH_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않음"),
    WRONG_USER(HttpStatus.UNAUTHORIZED, "사용자 정보가 일치하지 않음"),

    /* 404 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없음"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없음"),

    /* 409 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임"),

    /* 500 */
    MAIL_MSG_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 메시지 생성 에러"),
    MAIL_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 에러"),
    IMAGE_OPEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 입력 에러")
    ;
    private HttpStatus code;
    private final String message;
}
