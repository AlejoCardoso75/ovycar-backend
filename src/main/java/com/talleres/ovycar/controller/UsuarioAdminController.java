package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.CreateUsuarioRequestDTO;
import com.talleres.ovycar.dto.UsuarioDTO;
import com.talleres.ovycar.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Creación de usuarios en producción (solo ADMIN). La contraseña se guarda con BCrypt.
 * Ejemplo cuerpo JSON (como init-data):
 * {"username":"Alejo75","password":"Manchas123.","nombre":"Alejandro","apellido":"Cardoso","email":"alejandro.cardosoparra@gmail.com","rol":"ADMIN","activo":true}
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CreateUsuarioRequestDTO body) {
        try {
            UsuarioDTO creado = usuarioService.crearUsuarioAdmin(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
