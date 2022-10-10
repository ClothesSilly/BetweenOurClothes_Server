package com.betweenourclothes.domain.members;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@DynamicInsert
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=45, nullable=false)
    private String email;

    @Column(length=15, nullable=false)
    private String password;

    @Column(length=10, nullable=false)
    private String name;

    @Column(length=20, nullable=false)
    private String nickname;

    @Column(length=11, nullable=false)
    private String phone;

    @Column(columnDefinition = "varchar(100) default 'default_img.jpg'")
    private String image;

    @Column(nullable = false)
    private String role;

    @Builder
    public Members(String email, String password, String name, String nickname, String phone, String image, Role role){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.role = role.toString();
    }
}
