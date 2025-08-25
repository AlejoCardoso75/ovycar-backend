package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.AuthResponseDTO;
import com.talleres.ovycar.dto.LoginDTO;
import com.talleres.ovycar.dto.RegisterDTO;
import com.talleres.ovycar.entity.Usuario;
import com.talleres.ovycar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    
    public AuthResponseDTO login(LoginDTO loginDTO) {
        try {
            // Autenticar usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
                )
            );
            
            if (authentication.isAuthenticated()) {
                // Buscar usuario para obtener datos completos
                Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(loginDTO.getUsername());
                
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    
                    // Actualizar último acceso
                    usuario.setUltimoAcceso(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                    
                    // Generar JWT token
                    String token = jwtService.generateToken(
                        usuario.getUsername(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getRol()
                    );
                    
                    return new AuthResponseDTO(
                        token,
                        usuario.getUsername(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getRol(),
                        "Login exitoso",
                        true
                    );
                }
            }
            
            return new AuthResponseDTO(
                null, null, null, null, null,
                "Credenciales inválidas",
                false
            );
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "Usuario o contraseña incorrectos",
                false
            );
        } catch (org.springframework.security.authentication.DisabledException e) {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "La cuenta está deshabilitada",
                false
            );
        } catch (org.springframework.security.authentication.LockedException e) {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "La cuenta está bloqueada",
                false
            );
        } catch (Exception e) {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "Error de autenticación. Por favor, inténtelo de nuevo",
                false
            );
        }
    }
    
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        try {
            // Verificar si el username ya existe
            if (usuarioRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
                return new AuthResponseDTO(
                    null, null, null, null, null,
                    "El nombre de usuario ya existe",
                    false
                );
            }
            
            // Verificar si el email ya existe
            if (usuarioRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
                return new AuthResponseDTO(
                    null, null, null, null, null,
                    "El email ya está registrado",
                    false
                );
            }
            
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(registerDTO.getUsername());
            nuevoUsuario.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // Encriptar contraseña
            nuevoUsuario.setNombre(registerDTO.getNombre());
            nuevoUsuario.setApellido(registerDTO.getApellido());
            nuevoUsuario.setEmail(registerDTO.getEmail());
            nuevoUsuario.setRol(registerDTO.getRol() != null ? registerDTO.getRol() : "USER");
            nuevoUsuario.setActivo(true);
            nuevoUsuario.setFechaCreacion(LocalDateTime.now());
            
            // Guardar usuario
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
            
            // Generar JWT token
            String token = jwtService.generateToken(
                usuarioGuardado.getUsername(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellido(),
                usuarioGuardado.getRol()
            );
            
            return new AuthResponseDTO(
                token,
                usuarioGuardado.getUsername(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellido(),
                usuarioGuardado.getRol(),
                "Usuario registrado exitosamente",
                true
            );
            
        } catch (Exception e) {
            return new AuthResponseDTO(
                null, null, null, null, null,
                "Error en el registro: " + e.getMessage(),
                false
            );
        }
    }
    
    public AuthResponseDTO validateToken(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                // Validar JWT token
                if (jwtService.validateToken(token)) {
                    String username = jwtService.extractUsername(token);
                    String nombre = jwtService.extractNombre(token);
                    String apellido = jwtService.extractApellido(token);
                    String rol = jwtService.extractRol(token);
                    
                    return new AuthResponseDTO(
                        token,
                        username,
                        nombre,
                        apellido,
                        rol,
                        "Token válido",
                        true
                    );
                }
            } catch (Exception e) {
                // Token inválido o expirado
            }
        }
        
        return new AuthResponseDTO(
            null, null, null, null, null,
            "Token inválido o expirado",
            false
        );
    }
    
    public boolean isUserActive(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.isPresent() && usuario.get().getActivo();
    }

    public AuthResponseDTO extendSession(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                // Validar JWT token actual
                if (jwtService.validateToken(token)) {
                    String username = jwtService.extractUsername(token);
                    
                    // Verificar que el usuario esté activo
                    if (isUserActive(username)) {
                        // Buscar usuario para obtener datos completos
                        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
                        
                        if (usuarioOpt.isPresent()) {
                            Usuario usuario = usuarioOpt.get();
                            
                            // Actualizar último acceso
                            usuario.setUltimoAcceso(LocalDateTime.now());
                            usuarioRepository.save(usuario);
                            
                            // Generar nuevo JWT token
                            String newToken = jwtService.generateToken(
                                usuario.getUsername(),
                                usuario.getNombre(),
                                usuario.getApellido(),
                                usuario.getRol()
                            );
                            
                            return new AuthResponseDTO(
                                newToken,
                                usuario.getUsername(),
                                usuario.getNombre(),
                                usuario.getApellido(),
                                usuario.getRol(),
                                "Sesión extendida exitosamente",
                                true
                            );
                        }
                    }
                }
            } catch (Exception e) {
                // Token inválido o expirado
            }
        }
        
        return new AuthResponseDTO(
            null, null, null, null, null,
            "No se pudo extender la sesión",
            false
        );
    }
}
