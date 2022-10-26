package com.betweenourclothes.domain.members;

import com.betweenourclothes.domain.closets.Closets;
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
import java.util.List;
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

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private List<Closets> closetsPosts;

    @Builder
    public Members(String email, String password, String name, String nickname, String phone, Role role, String image){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.role = role;
        this.closetsPosts = new ArrayList<>();
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    public void updateRefreshToken(String token){
        this.refreshToken = token;
    }


    public void updateClosetsPosts(Closets post){
        this.closetsPosts.add(post);
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
