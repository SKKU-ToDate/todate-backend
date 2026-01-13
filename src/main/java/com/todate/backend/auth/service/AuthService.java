package com.todate.backend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.todate.backend.auth.dto.response.AccessTokenResponse;
import com.todate.backend.auth.jwt.GoogleTokenVerifier;
import com.todate.backend.auth.jwt.JwtUtil;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Google OAuth2 인증 서비스
 *
 * 학습 플로우:
 * 1. Google ID Token 검증
 * 2. Google 사용자 정보 추출 (sub, email, name)
 * 3. DB에서 사용자 조회 (googleId 기준)
 * 4. 없으면 신규 생성 (자동 회원가입)
 * 5. 자체 JWT 발급 (access + refresh)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;

    /**
     * Google OAuth2 로그인 처리
     *
     * @param idToken Flutter에서 받은 Google ID Token
     * @return AccessTokenResponse (accessToken, refreshToken)
     */
    public AccessTokenResponse loginWithGoogle(String idToken) {
        // 1. Google ID Token 검증
        GoogleIdToken.Payload payload;
        try {
            payload = googleTokenVerifier.verify(idToken);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid Google ID token: " + e.getMessage()
            );
        }

        // 2. Google 사용자 정보 추출
        String googleId = googleTokenVerifier.getGoogleId(payload);
        String email = googleTokenVerifier.getEmail(payload);
        String name = googleTokenVerifier.getName(payload);

        // 3. DB에서 Google 사용자 조회 (googleId로 찾기)
        User user = userRepository.findByGoogleId(googleId)
            .orElseGet(() -> {
                // 4. 없으면 신규 생성 (자동 회원가입)
                User newUser = User.createFromGoogle(email, googleId, name);
                return userRepository.save(newUser);
            });

        // 5. 자체 JWT 발급 (기존 JwtUtil 재사용)
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return AccessTokenResponse.of(accessToken, refreshToken, jwtUtil.accessTokenTtl(), user.getUsername(), user.getName());
    }
}
