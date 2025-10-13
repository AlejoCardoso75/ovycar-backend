package com.talleres.ovycar.config;

import com.talleres.ovycar.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, JwtConfig jwtConfig) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader(jwtConfig.TOKEN_HEADER);
        final String jwt;
        final String username;
        
        // Si no hay header de autorización o no empieza con "Bearer ", continuar
        if (authHeader == null || !authHeader.startsWith(jwtConfig.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraer el token (remover "Bearer ")
        jwt = authHeader.substring(jwtConfig.TOKEN_PREFIX.length());
        
        try {
            // Extraer username del token
            username = jwtService.extractUsername(jwt);
            
            // Si hay username y no hay autenticación actual
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Si el token es válido
                if (jwtService.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si hay error al procesar el token, continuar sin autenticación
            logger.error("Error processing JWT token", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
