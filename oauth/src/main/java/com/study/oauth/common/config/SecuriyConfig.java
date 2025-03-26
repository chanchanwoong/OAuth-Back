package com.study.oauth.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecuriyConfig {

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 프론트엔드가 별도 존재하므로 CSRF 공격 보안에 대해 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // Basic 인증은 사용자 이름과 비밀번호를 Base64 인코딩하여 인증값으로 사용한다. 여기서는 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 토큰 방식을 이용할 것이니 sessionManagement 비활성화
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 특정 url 패턴에 대해서는 security filter에서 제외(Authentication 객체를 안만들겠다는 의미)
                .authorizeHttpRequests(a -> a.requestMatchers(
                        "/member/create", "/member/doLogin", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll().anyRequest().authenticated())
                .build();
    }

    /**
     *  CORS 허용하는 도메인들을 명시한다.
     *  허용하지 않는 도메인의 요청은 에러를 반환시켜 프론트엔드와 통신이 불가능해진다.
     *  해당 메서드에 개발용 프론트엔드, 배포용 프론트엔드 도메인을 추가한다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));    // 개발 프론트엔드 도메인 허용
        configuration.setAllowedMethods(Arrays.asList("*"));    // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));    // 모든 헤더 값 허용
        configuration.setAllowCredentials(true);    // 자격 증명을 허용(Authorization 헤더를 허용 목적)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 url 패턴에 대해 CORS 허용 설정
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
