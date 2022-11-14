package com.betweenourclothes.web.dto.request.auth;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.Role;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Getter
@NoArgsConstructor
public class AuthSignUpRequestDto {
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @ApiParam(value = "이메일", required = true)
    private String email;
    @NotBlank(message = "비밀번호를 입력하세요.")
    //@Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$", message = "비밀번호는 숫자, 영문자, 특수문자를 조합하여 8자 이상 15자 이하로 입력하세요.")
    @ApiParam(value = "비밀번호 (숫자+영문자+특수문자 &~15자 이하)", required = true)
    private String password;

    @ApiParam(value = "본명", required = false)
    private String name;

    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min=1, max=15, message = "닉네임은 1자 이상 15자로 입력하세요.")
    @ApiParam(value = "닉네임", required =true)
    private String nickname;

    @ApiParam(value = "전화번호", required = false)
    private String phone;


    @Builder
    public AuthSignUpRequestDto(String email, String password, String name, String nickname, String phone) throws UnsupportedEncodingException {
        this.email = URLDecoder.decode(email, "utf-8");
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
    }

    public Members toEntity(String image, Role role){
        return Members.builder()
                .email(email)
                .password(password)
                .name(name)
                .image(image)
                .nickname(nickname)
                .phone(phone)
                .role(role)
                .build();
    }


}
