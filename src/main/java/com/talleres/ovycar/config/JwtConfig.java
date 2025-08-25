package com.talleres.ovycar.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    // Clave secreta para firmar JWT (en producción usar variable de entorno)
    public static final String SECRET_KEY = "OvyCarSecretKey2024TalleresOviedoJWTTokenAuthentication";
    
    // Tiempo de expiración: 24 horas en milisegundos
    public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 horas
    
    // Tiempo de inactividad: 30 minutos en milisegundos
    public static final long INACTIVITY_TIMEOUT = 30 * 60 * 1000; // 30 minutos
    
    // Header donde se enviará el token
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
