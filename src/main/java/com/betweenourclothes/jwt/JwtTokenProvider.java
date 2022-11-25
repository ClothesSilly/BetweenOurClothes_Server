package com.betweenourclothes.jwt;

import com.betweenourclothes.exception.ErrorCode;
import com.betweenourclothes.exception.customException.AuthSignInException;
import com.betweenourclothes.web.dto.response.auth.AuthTokenResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private Key key;
    private static long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60 * 3; //3시간
    private static long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; //1주일


    /*** 토큰을 암호화할 키 생성 ***/
    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /*** 토큰 생성 ***/
    public AuthTokenResponseDto createToken(Authentication authentication){

        // 권한 부여
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>authorities: "+ authorities);

        long now = new Date().getTime();

        // access token
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())                   // 토큰 용도
                .claim("auth", authorities)                       // 권한
                .setExpiration(new Date(now + ACCESS_TOKEN_VALID_TIME)) // 만료 기간
                .signWith(key, SignatureAlgorithm.HS256)                // 사용할 알고리즘과 key
                .compact();

        // refresh token
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return AuthTokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    /*** 토큰 검증 ***/
    public JwtStatus validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return JwtStatus.ACCESS;
        } catch (ExpiredJwtException e) {
            //e.printStackTrace();
            return JwtStatus.EXPIRED;
        } catch (Exception e){
            //e.printStackTrace();
        }
        return JwtStatus.DENIED;
    }

    /*** 토큰을 복호화해서 토큰 안의 정보 추출 ***/
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new AuthSignInException(ErrorCode.NOT_AUTHENTICATED);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 복호화
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
