package com.betweenourclothes.domain.members;

import com.betweenourclothes.domain.closets.Closets;
import com.betweenourclothes.domain.stores.Stores;
import com.betweenourclothes.domain.stores.StoresComments;
import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.ClosetsPostException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Column(columnDefinition = "varchar(300) default './src/main/resources/static/images/profile/default.png'")
    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    @OneToMany(mappedBy = "author", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Closets> closetsPosts;

    @OneToMany(mappedBy = "author", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stores> storesPosts;

    @OneToMany(mappedBy = "user", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StoresComments> storesComments;

    @OneToMany(mappedBy= "user", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MembersLikeStoresPost> storesLikes;

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
        this.storesPosts = new ArrayList<>();
        this.storesComments = new ArrayList<>();
        this.storesLikes = new ArrayList<>();
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


    public void updateStoresPosts(Stores post){
        this.storesPosts.add(post);
    }

    public void updateStoresComments(StoresComments comments){
        this.storesComments.add(comments);
    }

    public void updateStoresLikes(MembersLikeStoresPost post){
        this.storesLikes.add(post);
    }

    public byte[] toByte(int width, int height){
        try {
            BufferedImage bi = null;
            if(width == -1 && height == -1){
                bi = ImageIO.read(new File(this.image));
            } else{
                bi = Thumbnails.of(new File(this.image))
                        .size(width, height)
                        .asBufferedImage();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpeg", baos);
            byte[] imageByteArr = baos.toByteArray();
            baos.close();
            return imageByteArr;
        } catch (Exception e){
            throw new ClosetsPostException(ErrorCode.IMAGE_OPEN_ERROR);
        }
    }

    public void deleteStoresLikes(MembersLikeStoresPost entity) {
        this.storesLikes.remove(entity);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
