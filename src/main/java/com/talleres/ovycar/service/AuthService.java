package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.LoginDTO;
import com.talleres.ovycar.dto.AuthResponseDTO;
import com.talleres.ovycar.entity.Usuario;
import com.talleres.ovycar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    
    public AuthResponseDTO login(LoginDTO loginDTO) {
        try {
            // Buscar usuario por username y password
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndPassword(
                loginDTO.getUsername(), 
                loginDTO.getPassword()
            );
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                // Actualizar último acceso
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario);
                
                // Generar token simple (en producción usar JWT)
                String token = generateToken();
                
                return new AuthResponseDTO(
                    token,
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getRol(),
                    "Login exitoso",
                    true
                );
            } else {
                return new AuthResponseDTO(
                    null, null, null, null, null,
                    "Credenciales inválidas",
                    false
                );
            }
        } catch (Exception e) {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "Error en el servidor: " + e.getMessage(),
                false
            );
        }
    }
    
    public AuthResponseDTO validateToken(String token) {
        // En una implementación real, validarías el token JWT
        // Por ahora, simplemente verificamos que el token no esté vacío
        if (token != null && !token.isEmpty()) {
            // Buscar el usuario admin (por defecto para validación de token)
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername("admin");
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                return new AuthResponseDTO(
                    token,
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getRol(),
                    "Token válido",
                    true
                );
            } else {
                return new AuthResponseDTO(
                    token, null, null, null, null,
                    "Token válido",
                    true
                );
            }
        } else {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "Token inválido",
                false
            );
        }
    }
    
    private String generateToken() {
        // Generar token simple (en producción usar JWT)
        return UUID.randomUUID().toString();
    }
    
    public boolean isUserActive(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username).isPresent();
    }
}
