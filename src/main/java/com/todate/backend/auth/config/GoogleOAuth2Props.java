package com.todate.backend.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Google OAuth2 설정을 yml에서 주입받는 클래스
 *
 * application.yml:
 * google:
 *   oauth2:
 *     client-id: your-client-id.apps.googleusercontent.com
 */
@ConfigurationProperties(prefix = "google.oauth2")
public record GoogleOAuth2Props(
    String clientId
) {
}
