package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.CreateUsuarioRequestDTO;
import com.talleres.ovycar.dto.UsuarioDTO;
import com.talleres.ovycar.entity.Usuario;
import com.talleres.ovycar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDTO crearUsuarioAdmin(CreateUsuarioRequestDTO dto) {
        validar(dto);

        String username = dto.getUsername().trim();
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        String email = dto.getEmail().trim();
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String rol = normalizarRol(dto.getRol());
        Boolean activo = dto.getActivo() != null ? dto.getActivo() : true;

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setNombre(dto.getNombre().trim());
        u.setApellido(dto.getApellido().trim());
        u.setEmail(email);
        u.setRol(rol);
        u.setActivo(activo);
        u.setFechaCreacion(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(u);
        return toDto(guardado);
    }

    private static void validar(CreateUsuarioRequestDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
    }

    private static String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return "USER";
        }
        String r = rol.trim().toUpperCase(Locale.ROOT);
        if (!"ADMIN".equals(r) && !"USER".equals(r)) {
            throw new IllegalArgumentException("Rol no válido. Use ADMIN o USER");
        }
        return r;
    }

    private static UsuarioDTO toDto(Usuario u) {
        return new UsuarioDTO(
                u.getId(),
                u.getUsername(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getRol(),
                u.getActivo(),
                u.getFechaCreacion(),
                u.getUltimoAcceso()
        );
    }
}
