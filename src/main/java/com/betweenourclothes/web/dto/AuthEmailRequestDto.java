package com.betweenourclothes.web.dto;

import com.betweenourclothes.domain.members.Email;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Random;

@Getter
@NoArgsConstructor
public class AuthEmailRequestDto {

    @NotBlank(message = "이메일을 입력하세요.")
    @javax.validation.constraints.Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    private String code;
    private String status;


    public Email toEntity(){
        return Email.builder().email(email).code(code).status(status).build();
    }

    private String createAuthCode(){
        int leftLimit = 48;
        int rightLimit = 122;
        int length = 6;

        Random random = new Random();
        String key = random.ints(leftLimit, rightLimit+1)
                .filter(i -> (i<=57 || i>=65) && (i <=90 || i>=97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return key;
    }

    private String booleanConverter(boolean value){
        if(value){
            return "Y";
        } else{
            return "N";
        }
    }

    @Builder
    public AuthEmailRequestDto(String email){
        this.email = email;
        this.code = createAuthCode();
        this.status = booleanConverter(false);
    }

    @Builder
    public AuthEmailRequestDto(String email, String code){
        this.email = email;
        this.code = code;
        this.status = booleanConverter(false);
    }

}
