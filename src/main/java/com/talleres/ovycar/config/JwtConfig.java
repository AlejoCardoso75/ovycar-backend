package com.talleres.ovycar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    public static final long INACTIVITY_TIMEOUT = 30 * 60 * 1000;
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public String getSecretKey() {
        return secretKey;
    }
}
