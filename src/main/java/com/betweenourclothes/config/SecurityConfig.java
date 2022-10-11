package com.betweenourclothes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity //기본적인 Web 보안 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .formLogin().disable() // 기본 제공 form 비활성화
                .httpBasic().disable() // 기본 alert창 비활성화
                .csrf().disable() // api 서버 개발 --> 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용
                .and()
                .authorizeRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근제한 설정
                .antMatchers("/api/v1/auth/sign-up",
                                        "/api/v1/auth/sign-up/image",
                        "/api/v1/auth/sign-up/email",
                        "/api/v1/auth/sign-up/code")
                .permitAll()
                .anyRequest()  // 나머지 요청들은 모두 인증
                .authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
