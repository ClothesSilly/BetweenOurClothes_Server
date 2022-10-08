package com.betweenourclothes.web.dto;

import com.betweenourclothes.domain.members.Members;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MembersRegisterRequestDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String image;

    @Builder
    public MembersRegisterRequestDto(String email, String password, String name, String nickname, String phone, String image){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public Members toEntity(){
        return Members.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .phone(phone).build();
    }
}
