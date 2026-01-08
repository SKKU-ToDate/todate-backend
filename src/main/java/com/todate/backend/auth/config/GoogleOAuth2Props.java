package com.todate.backend.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Google OAuth2 설정을 yml에서 주입받는 클래스
 *
 * application.yml:
 * google:
 *   oauth2:
 *     client-ids: android-id,ios-id
 *
 * 여러 플랫폼(Android, iOS)의 Client ID를 리스트로 관리
 */
@ConfigurationProperties(prefix = "google.oauth2")
public record GoogleOAuth2Props(
    List<String> clientIds
) {
}
