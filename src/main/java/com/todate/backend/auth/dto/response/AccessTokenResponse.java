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

    @JsonProperty("token_type")
    private final String tokenType;

    @JsonProperty("expires_in")
    private final long expiresInSeconds;

    public static AccessTokenResponse of(String accessToken, Duration ttl) {
        long expiresIn = ttl != null ? ttl.toSeconds() : 0L;
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresInSeconds(expiresIn)
                .build();
    }
}
