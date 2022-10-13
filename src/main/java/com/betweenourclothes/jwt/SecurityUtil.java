package com.betweenourclothes.jwt;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class SecurityUtil {

    // 어떤 Member가 API를 요청했는지 조회하는 코드
    // 인증된 Member들이 security context에 저장이 됨
    // Member의 정보가 헤더에 담겨져 오면 그거 떼서 비교
    public static String getMemberEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw  new RuntimeException("Security Context에 인증정보가 없음");
        }

        return authentication.getName();
    }
}
