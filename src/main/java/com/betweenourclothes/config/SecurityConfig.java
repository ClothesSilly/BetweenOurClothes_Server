package com.betweenourclothes.config;

import com.betweenourclothes.jwt.JwtAuthenticationFilter;
import com.betweenourclothes.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //기본적인 Web 보안 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .formLogin().disable() // 기본 제공 form 비활성화
                .httpBasic().disable() // 기본 alert창 비활성화
                .csrf().disable() // api 서버 개발 --> 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용
                .and()
                .authorizeRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근제한 설정
                    //.antMatchers("/api/v1/closets/post/thumbnails/display").permitAll()
                    .antMatchers("/api/v1/auth/**").permitAll()
                    .antMatchers("/api/v1/closets/**", "/api/v1/stores/**").hasAuthority("ROLE_USER")
                    .antMatchers("/v2/api-docs","/swagger-ui.html/**", "/swagger-resources/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                 // UPAF 전에 AuthenticationFilter 추가
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //web.ignoring().antMatchers("/swagger-resources/", "/webjars/") .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
