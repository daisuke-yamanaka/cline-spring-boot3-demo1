package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * セキュリティ設定クラス
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * セキュリティフィルターチェーンの設定
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF対策を有効化
            .csrf(csrf -> csrf
                // CSRFトークンをCookieで管理
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // 認証・認可の設定
            .authorizeHttpRequests(auth -> auth
                // 静的リソースへのアクセスを許可
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // その他のリクエストは認証不要（今回は認証機能は実装しない）
                .anyRequest().permitAll()
            )
            // XSS対策のためにX-Frame-Optionsを設定
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
