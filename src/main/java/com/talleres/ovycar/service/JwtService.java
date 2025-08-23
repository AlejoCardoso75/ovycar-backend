package com.talleres.ovycar.service;

import com.talleres.ovycar.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    
    private final SecretKey secretKey = Keys.hmacShaKeyFor(JwtConfig.SECRET_KEY.getBytes());
    
    public String generateToken(String username, String nombre, String apellido, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", nombre);
        claims.put("apellido", apellido);
        claims.put("rol", rol);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConfig.EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String extractNombre(String token) {
        return extractClaim(token, claims -> claims.get("nombre", String.class));
    }
    
    public String extractApellido(String token) {
        return extractClaim(token, claims -> claims.get("apellido", String.class));
    }
    
    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }
    
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
