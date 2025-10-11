package com.todate.backend.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Duration accessTokenValidity;
    private final Duration refreshTokenValidity;

    public JwtUtil(JwtProps props) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(props.secret());     // Base64 우선
        } catch (IllegalArgumentException e) {
            keyBytes = props.secret().getBytes(StandardCharsets.UTF_8); // 평문 fallback
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be >= 32 bytes (256 bits).");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidity = props.accessValid();
        this.refreshTokenValidity = props.refreshValid();
    }

    public String generateAccessToken(String subject) {
        return generateToken(subject, "access", accessTokenValidity);
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, "refresh", refreshTokenValidity);
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parse(token).get("type"));
    }

    public String extractSubject(String token) {
        return parse(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return parse(token).getExpiration();
    }

    public boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Duration accessTokenTtl() {
        return accessTokenValidity;
    }

    public Duration refreshTokenTtl() {
        return refreshTokenValidity;
    }

    private String generateToken(String subject, String type, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .claims(Map.of("type", type))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private io.jsonwebtoken.Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
