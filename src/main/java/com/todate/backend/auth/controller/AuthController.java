package com.todate.backend.auth.controller;

import com.todate.backend.auth.dto.request.GoogleLoginRequest;
import com.todate.backend.auth.dto.response.AccessTokenResponse;
import com.todate.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Google OAuth2 인증 컨트롤러
 *
 * 단일 엔드포인트: POST /auth/google
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Google OAuth2 로그인 엔드포인트
     *
     * 요청 예시:
     * POST /auth/google
     * Content-Type: application/json
     * {
     *   "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFlOWdkazcifQ..."
     * }
     *
     * 응답 예시:
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "ttl": "PT24H"
     * }
     *
     * @param request Google ID Token 포함
     * @return 자체 JWT 토큰 (access + refresh)
     */
    @PostMapping("/google")
    public ResponseEntity<AccessTokenResponse> googleLogin(
        @Valid @RequestBody GoogleLoginRequest request
    ) {
        AccessTokenResponse response = authService.loginWithGoogle(request.idToken());
        return ResponseEntity.ok(response);
    }
}
