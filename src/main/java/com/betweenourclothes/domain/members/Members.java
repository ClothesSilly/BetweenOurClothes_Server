package com.betweenourclothes.domain.members;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignInException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
@DynamicInsert
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Members implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=45, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(length=10, nullable=false)
    private String name;

    @Column(length=20, nullable=false)
    private String nickname;

    @Column(length=11, nullable=false)
    private String phone;

    @Column(columnDefinition = "varchar(300) default 'C:/Users/user1/Desktop/송아/캡스톤/repo/between-our-clothes-server/src/main/resources/static/images/profile/default.png'")
    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    @Builder
    public Members(String email, String password, String name, String nickname, String phone){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
    public void updateRefreshToken(String token){
        this.refreshToken = token;
    }

    public void updateRole(Role role){
        this.role = role;
    }

    public void updateImage(MultipartFile img){
        try{
            // 업로드 파일 이름 생성
            // 업로드 파일 식별을 위한 uuid 생성
            String uuid = UUID.randomUUID().toString();
            String path = new File("./src/main/resources/static/images/profile").getAbsolutePath();
            String uploadedFileName = "profile-" + uuid;

            // 확장자
            String extension = '.' + img.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");

            // 파일 객체 생성: 파일이 저장될 디렉토리를 만듦
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }

            // 파일 객체 생성: 업로드될 파일을 위한 것
            file = new File(path+"/"+uploadedFileName+extension);

            // 전송 후, 파일 경로 반환
            img.transferTo(file);
            this.image = file.getAbsolutePath();
        } catch(NullPointerException e){
            throw new AuthSignInException(ErrorCode.REQUEST_FORMAT_ERROR);
        } catch (IOException e) {
            throw new AuthSignInException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(role.name()));
        return auth;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
