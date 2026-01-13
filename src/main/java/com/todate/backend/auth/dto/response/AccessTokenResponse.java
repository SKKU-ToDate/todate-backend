package com.todate.backend.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    @JsonProperty("token_type")
    private final String tokenType;

    @JsonProperty("expires_in")
    private final long expiresInSeconds;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("name")
    private final String name;

    // For backward compatibility (single access token)
    public static AccessTokenResponse of(String accessToken, Duration ttl, String username, String name) {
        long expiresIn = ttl != null ? ttl.toSeconds() : 0L;
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresInSeconds(expiresIn)
                .username(username)
                .name(name)
                .build();
    }

    // For Google OAuth2 (access + refresh tokens)
    public static AccessTokenResponse of(String accessToken, String refreshToken, Duration ttl, String username, String name) {
        long expiresIn = ttl != null ? ttl.toSeconds() : 0L;
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(expiresIn)
                .username(username)
                .name(name)
                .build();
    }
}
