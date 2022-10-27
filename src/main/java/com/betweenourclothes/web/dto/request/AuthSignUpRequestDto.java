package com.betweenourclothes.web.dto.request;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class AuthSignUpRequestDto {
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$", message = "비밀번호는 숫자, 영문자, 특수문자를 조합하여 8자 이상 15자 이하로 입력하세요.")
    private String password;
    private String name;
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min=1, max=15, message = "닉네임은 1자 이상 15자로 입력하세요.")
    private String nickname;
    private String phone;

    private Role role;


    public void setRole(Role role){
        this.role = role;
    }

    @Builder
    public AuthSignUpRequestDto(String email, String password, String name, String nickname, String phone, Role role){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.role = Role.ROLE_USER;
    }

    public Members toEntity(String image){
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
