package com.betweenourclothes.domain.members;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

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
    public Members(String email, String password, String name, String nickname, String phone, String image, Role role){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.role = role;
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password =passwordEncoder.encode(password);
    }

    public void updateRefreshToken(String token){
        this.refreshToken = token;
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
