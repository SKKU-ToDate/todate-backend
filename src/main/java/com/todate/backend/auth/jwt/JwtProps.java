package com.todate.backend.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.auth.jwt")
public record JwtProps(
        String secret,
        Duration accessValid,
        Duration refreshValid
) {}