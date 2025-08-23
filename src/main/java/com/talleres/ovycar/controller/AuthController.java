package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.LoginDTO;
import com.talleres.ovycar.dto.AuthResponseDTO;
import com.talleres.ovycar.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<AuthResponseDTO> validateToken(@RequestHeader("Authorization") String token) {
        // Remover "Bearer " si está presente
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        AuthResponseDTO response = authService.validateToken(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout() {
        AuthResponseDTO response = new AuthResponseDTO(
            null, null, null, null, null,
            "Logout exitoso",
            true
        );
        return ResponseEntity.ok(response);
    }
}
