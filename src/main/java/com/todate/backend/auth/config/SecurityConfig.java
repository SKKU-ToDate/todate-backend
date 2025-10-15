package com.todate.backend.auth.config;

import com.todate.backend.auth.core.BasicAuthenticationProvider;
import com.todate.backend.auth.jwt.JwtAuthenticationFilter;
import com.todate.backend.auth.jwt.JwtProps;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProps.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final Environment env;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
        BasicAuthenticationProvider basicAuthenticationProvider) throws Exception {
        if (env.acceptsProfiles(Profiles.of("local"))) {
            // local h2-console 인증 제외 및 iframe 허용
            http.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
            http.authorizeHttpRequests(a -> a.requestMatchers("/h2-console/**").permitAll());
        } else {
            // 이외 경우 iframe deny
            http.headers(h -> {
                h.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
                h.contentSecurityPolicy(c -> c.policyDirectives("frame-ancestors 'none'"));
            });
        }

        http
            // jwt 사용하므로 csrf 토큰 필요없음
            .csrf(AbstractHttpConfigurer::disable)
            // 내장 서버 세션 필요 없음
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 기본 로그인 폼 필요 없음
            .formLogin(AbstractHttpConfigurer::disable)
            // Basic 인증 끄기
            .httpBasic(AbstractHttpConfigurer::disable)
            // 요청 endpoint 별 인증 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 제외할 endpoint 명시
                .requestMatchers("/auth/**", "/error").permitAll()
                // 이외 request는 모두 인증
                .anyRequest().authenticated()
            )
            .authenticationProvider(basicAuthenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 에러 핸들링
            .exceptionHandling(ex -> ex
                    // 인증 실패 401
                    .authenticationEntryPoint((req, res, e) -> {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"message\":\"Unauthorized\"}");
                    })
//                        // 인가 실패 403 (아직 필요 없음)
//                        .accessDeniedHandler((req, res, e) -> {
//                            res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
//                            res.setContentType("application/json");
//                            res.getWriter().write("{\"message\":\"Forbidden\"}");
//                        })
            );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        BasicAuthenticationProvider basicAuthenticationProvider) {
        return new ProviderManager(basicAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
