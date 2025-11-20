package com.todate.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Google 로그인 요청 DTO
 *
 * Flutter에서 전송하는 데이터:
 * {
 *   "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij..."
 * }
 */
public record GoogleLoginRequest(
    @NotBlank(message = "ID Token is required")
    String idToken
) {
}
