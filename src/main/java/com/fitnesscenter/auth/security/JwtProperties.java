package com.fitnesscenter.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")   //make sure to enable the config at the project main class
public record JwtProperties(

        String secret,

        long accessTokenExpiration,

        long refreshTokenExpiration
) {
}