package com.talleres.ovycar.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    // Clave secreta para firmar JWT (en producción usar variable de entorno)
    public static final String SECRET_KEY = "OvyCarSecretKey2024TalleresOviedoJWTTokenAuthentication";
    
    // Tiempo de expiración: 5 minutos en milisegundos
    public static final long EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutos
    
    // Header donde se enviará el token
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
